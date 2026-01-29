package com.epam.gym_crm.service;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ITraineeService {
    Trainee create(Trainee trainee);

    Trainee updateProfile(String username,
                          String newFirstName,
                          String newLastName,
                          LocalDate newDateOfBirth,
                          String newAddress);

    Trainee getProfile(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainee setActive(String username, boolean active);

    boolean deleteByUsername(String username);

    List<Training> getTrainings(String username);

    List<Training> findTrainingsByDateRange(String username,
                                            LocalDateTime from, LocalDateTime to);

    List<Training> findTrainingsByTrainerName(String traineeUsername, String trainerName);

    List<Training> findTrainingsByType(String username, String trainingTypeName);

    List<Trainee> findAll();

    List<Trainer> findNotAssignedTrainers(String username);

    Trainee updateTrainers(String username, List<String> trainerUsernames);

}
