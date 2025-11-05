package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.metrics.GymMetrics;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.repository.ITrainingRepository;
import com.epam.gym_crm.service.IAuthService;
import com.epam.gym_crm.service.ITrainingService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements ITrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final ITrainingRepository trainingRepository;
    private final ITrainerRepository trainerRepository;
    private final ITraineeRepository traineeRepository;
    private final IAuthService authService;

    public TrainingServiceImpl(ITrainingRepository trainingRepository,
                               ITrainerRepository trainerRepository,
                               ITraineeRepository traineeRepository,
                               IAuthService authService
                               ) {

        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.authService = authService;
    }

    @Transactional
    @Override
    public Training create(Training training) {
        if (training.getTrainee() == null || training.getTrainer() == null) {
            throw new IllegalArgumentException("trainee and trainer must be set");
        }
        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("trainingType must be set");
        }
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("trainingDate must not be null");
        }
        if (training.getTrainingDuration() == null || training.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("duration must be > 0");
        }
        trainerRepository.findById(training.getTrainer().getId())
                .orElseThrow(() -> new NoSuchElementException("Trainer not found: " +
                                                              training.getTrainer().getId()));
        traineeRepository.findById(training.getTrainee().getId())
                .orElseThrow(() -> new NoSuchElementException("Trainee not found: " +
                                                              training.getTrainee().getId()));

//        Training saved = trainingRepository.create(training);
        Training saved = create(training);
        log.info("create(): training id={} (traineeId={}, trainerId={})",
                saved.getId(), saved.getTrainee().getId(), saved.getTrainer().getId());
        return saved;
    }

    @Override
    public Training update(Training training) {
        if (training.getId() == null) {
            throw new IllegalArgumentException("id must be set for update");
        }
        Training updated = trainingRepository.update(training);
        log.info("update(): id={}", updated.getId());
        return updated;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("findById(): id={}", id);
        Optional<Training> result = trainingRepository.findById(id);
        if (result.isEmpty()) {
            log.warn("findById(): training id={} not found", id);
        }
        return result;
    }

    @Override
    public List<Training> findAll() {
        List<Training> all = trainingRepository.findAll();
        log.debug("findAll(): size={}", all.size());
        return all;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean ok = trainingRepository.deleteById(id);
        if (ok) {
            log.info("deleteById(): training deleted id={}", id);
        } else {
            log.warn("deleteById(): training id={} not found", id);
        }
        return ok;
    }

    @Counted(
            value = "gym.training",
            description = "How many trainings were created"
    )
    @Timed(
            value = "gym.training.save",
            description = "Time to create a training",
            histogram = true
    )
    @Override
    public Training addTraining(String trainerUsername, String trainerPassword,
                                String traineeUsername, String trainingName,
                                String trainingTypeName, LocalDateTime trainingDate,
                                int durationMinutes) {
        if (!authService.verifyTrainer(trainerUsername, trainerPassword)) {
            throw new RuntimeException("Authentication failed");
        }
        if (traineeUsername == null || traineeUsername.isBlank())
            throw new IllegalArgumentException("traineeUsername must not be blank");
        if (trainingName == null || trainingName.isBlank())
            throw new IllegalArgumentException("trainingName must not be blank");
        if (trainingTypeName == null || trainingTypeName.isBlank())
            throw new IllegalArgumentException("trainingTypeName must not be blank");
        if (trainingDate == null)
            throw new IllegalArgumentException("trainingDate must not be null");
        if (durationMinutes <= 0)
            throw new IllegalArgumentException("duration must be > 0");

        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        Trainee trainee = traineeRepository.findByUsername(traineeUsername.trim())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        boolean assigned = trainee.getTrainers().stream()
                .anyMatch(t -> t.getId().equals(trainer.getId()));
        if (!assigned) {
            throw new RuntimeException("Trainer is not assigned to this trainee");
        }
        var type = trainingRepository.findTypeByName(trainingTypeName.trim())
                .orElseThrow(() -> new RuntimeException("TrainingType not found: " + trainingTypeName));

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingName(trainingName.trim());
        training.setTrainingType(type);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(durationMinutes);

        return trainingRepository.create(training);
    }
}
