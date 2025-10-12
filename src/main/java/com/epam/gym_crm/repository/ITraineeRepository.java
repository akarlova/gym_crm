package com.epam.gym_crm.repository;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITraineeRepository extends ICrudRepository<Trainee, Long> {
    Trainee save(Trainee t);

    Optional<Trainee> findByUsername(String username);

    void deleteByUsername(String username);

    List<Training> findTrainings(String traineeUsername);

    List<Training> findTrainingsByDateRange(String traineeUsername,
                                            LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTrainerName(String traineeUsername, String trainerName);

    List<Training> findTrainingsByType(String traineeUsername, String trainingTypeName);

    List<Trainer> findNotAssignedTrainers(String traineeUsername);

}
