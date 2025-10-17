package com.epam.gym_crm.service;

public interface IAuthService {
    boolean verifyTrainee(String username, String rawPassword);

    boolean verifyTrainer(String username, String rawPassword);
}
