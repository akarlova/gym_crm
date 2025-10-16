package com.epam.gym_crm.repository;


import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;

import java.util.Optional;

public interface ITrainingRepository extends ICrudRepository<Training, Long> {
    Optional<TrainingType> findTypeByName(String name);
}
