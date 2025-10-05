package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface ITraineeService {
    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();

    boolean deleteById(Long id);
}
