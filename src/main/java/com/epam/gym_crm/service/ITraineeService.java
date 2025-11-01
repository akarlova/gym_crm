package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITraineeService {
    Trainee create(Trainee trainee);

    Trainee updateProfile(String username,
                          String password,
                          String newFirstName,
                          String newLastName,
                          LocalDate newDateOfBirth,
                          String newAddress);


    Trainee getProfile(String username, String password);

    Trainee getProfile(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainee setActive(String username, String password, boolean active);

    boolean deleteByUsername(String username, String password);

    List<Training> getTrainings(String username, String password);

    List<Training> findTrainingsByDateRange(String username, String password,
                                            LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTrainerName(String traineeUsername, String traineePassword,
                                              String trainerName);

    List<Training> findTrainingsByType(String username, String password,
                                       String trainingTypeName);

    List<Trainee> findAll();

    List<Trainer> findNotAssignedTrainers(String username, String password);

    Trainee updateTrainers(String username, String password, List<String> trainerUsernames);
}
