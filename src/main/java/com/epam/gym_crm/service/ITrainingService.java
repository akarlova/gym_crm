package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Training;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITrainingService {
    Training create(Training training);

    Training update(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    boolean deleteById(Long id);

    Training addTraining(String trainerUsername,
                         String traineeUsername,
                         String trainingName,
                         String trainingTypeName,
                         LocalDateTime trainingDate,
                         int durationMinutes);
}
