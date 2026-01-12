package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.TrainingType;

import java.util.List;

public interface ITrainingTypeService {
    List<TrainingType> findAll();
}
