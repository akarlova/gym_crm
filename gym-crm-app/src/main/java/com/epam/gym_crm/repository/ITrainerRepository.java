package com.epam.gym_crm.repository;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITrainerRepository extends ICrudRepository<Trainer, Long> {
    Trainer save(Trainer t);

    Optional<Trainer> findByUsername(String username);

    List<Training> findTrainings(String trainerUsername);

    List<Training> findTrainingsByDateRange(String trainerUsername, LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTraineeName(String trainerUsername, String traineeName);

    List<Training> findTrainingsByType(String trainerUsername, String trainingTypeName);
}
