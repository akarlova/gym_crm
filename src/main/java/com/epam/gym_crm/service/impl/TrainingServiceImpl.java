package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.TraineeDao;
import com.epam.gym_crm.dao.TrainerDao;
import com.epam.gym_crm.dao.TrainingDao;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.TrainingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    public TrainingServiceImpl(TrainingDao trainingDao,
                               TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    @Override
    public Training create(Training training) {
        if (training.getTraineeId() == null || training.getTrainerId() == null) {
            throw new IllegalArgumentException("traineeId and trainerId are required");
        }
        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            throw new NoSuchElementException("Trainee is not found: " + training.getTraineeId());
        }
        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            throw new NoSuchElementException("Trainer is not found: " + training.getTrainerId());
        }
        return trainingDao.create(training);
    }

    @Override
    public Training update(Training training) {
        return trainingDao.update(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        return trainingDao.findById(id);
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(trainingDao.findAll());
    }

    @Override
    public boolean deleteById(Long id) {
        return trainingDao.deleteById(id);
    }
}
