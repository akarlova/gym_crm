package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITrainerService {
    Trainer create(Trainer trainer);

    Trainer updateProfile(String username,
                          String password,
                          String newFirstName,
                          String newLastName
    );


    Trainer getProfile(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainer setActive(String username, String password, boolean active);

    List<Training> getTrainings(String username, String password);

    List<Training> findTrainingsByDateRange(String username, String password,
                                            LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTraineeName(String trainerUsername, String trainerPassword,
                                              String traineeName);

    List<Training> findTrainingsByType(String username, String password,
                                       String trainingTypeName);

    List<Trainer> findAll();
}
