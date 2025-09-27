package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();

    boolean deleteById(Long id);
}
