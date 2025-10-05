package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.ITraineeDao;
import com.epam.gym_crm.dao.ITrainerDao;
import com.epam.gym_crm.dao.ITrainingDao;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.ITrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements ITrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final ITrainingDao trainingDao;
    private final ITrainerDao trainerDao;
    private final ITraineeDao traineeDao;

    public TrainingServiceImpl(ITrainingDao trainingDao,
                               ITrainerDao trainerDao, ITraineeDao traineeDao) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    @Override
    public Training create(Training training) {
        log.debug("create(): traineeId={}, trainerId={}, type={}, date={}",
                training.getTraineeId(), training.getTrainerId(), training.getTrainingType(),
                training.getTrainingDate());
        if (training.getTraineeId() == null || training.getTrainerId() == null) {
            log.warn("create(): missing traineeId/trainerId");
            throw new IllegalArgumentException("traineeId and trainerId are required");
        }
        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            throw new NoSuchElementException("Trainee is not found: " + training.getTraineeId());
        }
        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            throw new NoSuchElementException("Trainer is not found: " + training.getTrainerId());
        }
        var saved = trainingDao.create(training);
        log.info("create(): training created id={} (traineeId={}, trainerId={})",
                saved.getId(), saved.getTraineeId(), saved.getTrainerId());
        return saved;
    }

    @Override
    public Training update(Training training) {
        log.debug("update(): id={}", training.getId());
        var updated = trainingDao.update(training);
        log.info("update(): id={}", updated.getId());
        return updated;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("findById(): id={}", id);
        Optional<Training> result = trainingDao.findById(id);
        if (result.isEmpty()) {
            log.warn("findById(): training id={} not found", id);
        }
        return result;
    }

    @Override
    public List<Training> findAll() {
        List<Training> all = trainingDao.findAll();
        log.debug("findAll: size ={}", all.size());
        return all;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean ok = trainingDao.deleteById(id);
        if (ok) {
            log.info("deleteById: training deleted id={}", id);
        } else {
            log.warn("deleteById: training id={} not found", id);
        }
        return ok;
    }
}
