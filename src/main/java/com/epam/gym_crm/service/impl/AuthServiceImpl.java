package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.service.IAuthService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthServiceImpl implements IAuthService {
    private final ITraineeRepository traineeRepo;
    private final ITrainerRepository trainerRepo;

    public AuthServiceImpl(ITraineeRepository traineeRepo, ITrainerRepository trainerRepo) {
        this.traineeRepo = traineeRepo;
        this.trainerRepo = trainerRepo;
    }

    @Override
    public boolean verifyTrainee(String username, String rawPassword) {
        if (isBlank(username) || isBlank(rawPassword)) {
            return false;
        }
        return traineeRepo.findByUsername(username)
                .map(Trainee::getUser)
//                .filter(User::isActive)
                .map(User::getPassword)
                .filter(pwd -> Objects.equals(pwd, rawPassword))
                .isPresent();
    }

    @Override
    public boolean verifyTrainer(String username, String rawPassword) {
        if (isBlank(username) || isBlank(rawPassword)) {
            return false;
        }
        return trainerRepo.findByUsername(username)
                .map(Trainer::getUser)
//                .filter(User::isActive)
                .map(User::getPassword)
                .filter(pwd -> Objects.equals(pwd, rawPassword))
                .isPresent();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
