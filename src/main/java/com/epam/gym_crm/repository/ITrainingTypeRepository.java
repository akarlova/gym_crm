package com.epam.gym_crm.repository;

import com.epam.gym_crm.domain.TrainingType;

import java.util.List;

public interface ITrainingTypeRepository {
    List<TrainingType> findAll();
}
