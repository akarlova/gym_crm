package com.epam.gym_crm.service;

import com.epam.gym_crm.config.AppConfig;
import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(AppConfig.class)
public class TrainingServiceTest {
    private final ITrainingService trainingService;
    private final ITraineeService traineeService;
    private final ITrainerService trainerService;

    private final Map<Long, Training> trainingStorage;
    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;

    @Autowired
    public TrainingServiceTest(ITrainingService trainingService, ITraineeService traineeService,
                               ITrainerService trainerService,
                               @Qualifier("trainingStorage") Map<Long, Training> trainingStorage,
                               @Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage,
                               @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;

        this.trainingStorage = trainingStorage;
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
    }

    @BeforeEach
    void clean() {
        trainingStorage.clear();
        traineeStorage.clear();
        trainerStorage.clear();
    }

    @Test
    void createTrainingWhenParticipantsExist() {
        var trainee = new Trainee();
        trainee.setFirstName("Jon");
        trainee.setLastName("Snow");
        var trainer = new Trainer();
        trainer.setFirstName("Ned");
        trainer.setLastName("Stark");

        trainee = traineeService.create(trainee);
        trainer = trainerService.create(trainer);

        var training = new Training();
        training.setTraineeId(trainee.getId());
        training.setTrainerId(trainer.getId());
        training.setTrainingName("Private class");
        training.setTrainingType(TrainingType.CARDIO);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(Duration.ofMinutes(50));

        training = trainingService.create(training);

        assertNotNull(training.getId());
        assertEquals(1, trainingService.findAll().size());
    }

    @Test
    void failsIfTraineeOrTrainerMissed() {
        var training = new Training();
        training.setTraineeId(45L);
        training.setTrainerId(3L);
        training.setTrainingName("Bad class");
        training.setTrainingType(TrainingType.OTHER);
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(Duration.ofMinutes(20));

        assertThrows(NoSuchElementException.class, () ->trainingService.create(training));
    }
}
