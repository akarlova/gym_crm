package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import java.time.LocalDateTime;
import java.util.List;


public interface ITrainerService {
    Trainer create(Trainer trainer);

    Trainer updateProfile(String username,
                          String newFirstName,
                          String newLastName);


    Trainer getProfile(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainer setActive(String username, boolean active);

    List<Training> getTrainings(String username);

    List<Training> findTrainingsByDateRange(String username,
                                            LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTraineeName(String trainerUsername, String traineeName);

    List<Training> findTrainingsByType(String username, String trainingTypeName);

    List<Trainer> findAll();

}
