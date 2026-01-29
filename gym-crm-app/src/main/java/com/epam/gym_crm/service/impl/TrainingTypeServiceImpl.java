package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.repository.ITrainingTypeRepository;
import com.epam.gym_crm.service.ITrainingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements ITrainingTypeService {
    private final ITrainingTypeRepository repo;

    public TrainingTypeServiceImpl(ITrainingTypeRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TrainingType> findAll() {
        return repo.findAll();
    }
}
