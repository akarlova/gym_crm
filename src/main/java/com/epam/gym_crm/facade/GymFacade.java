package com.epam.gym_crm.facade;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.metrics.GymMetrics;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.service.ITrainingService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class GymFacade {
    private final ITraineeService traineeService;
    private final ITrainerService trainerService;
    private final ITrainingService trainingService;


    public GymFacade(ITraineeService traineeService,
                     ITrainerService trainerService,
                     ITrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    //Trainee
    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee getTraineeProfile(String username, String password) {
        return traineeService.getProfile(username, password);
    }


    public Trainee updateTraineeProfile(String username, String password,
                                        String newFirstName, String newLastName,
                                        LocalDate newDateOfBirth, String newAddress) {
        return traineeService.updateProfile(username, password, newFirstName,
                newLastName, newDateOfBirth, newAddress);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee setTraineeActive(String username, String password, boolean isActive) {
        return traineeService.setActive(username, password, isActive);
    }

    public List<Training> getTraineeTrainings(String username, String password) {
        return traineeService.getTrainings(username, password);
    }

    public List<Training> getTraineeTrainingsByDate(String username, String password,
                                                    LocalDateTime from, LocalDateTime to) {
        return traineeService.findTrainingsByDateRange(username, password, from, to);
    }

    public List<Training> getTraineeTrainingsByTrainer(String username, String password, String trainerQuery) {
        return traineeService.findTrainingsByTrainerName(username, password, trainerQuery);
    }

    public List<Training> getTraineeTrainingsByType(String username, String password, String typeName) {
        return traineeService.findTrainingsByType(username, password, typeName);
    }


    public List<Trainee> findAllTrainees() {
        return traineeService.findAll();
    }

    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        return traineeService.findNotAssignedTrainers(username, password);
    }

    public Trainee updateTraineeTrainers(String username, String password, List<String> trainerUsernames) {
        return traineeService.updateTrainers(username, password, trainerUsernames);
    }

    public boolean deleteTraineeByUsername(String username, String password) {
        return traineeService.deleteByUsername(username, password);
    }

    //Trainer
    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer getTrainerProfile(String username, String password) {
        return trainerService.getProfile(username, password);
    }

    public Trainer updateTrainerProfile(String username, String password,
                                        String newFirstName, String newLastName) {
        return trainerService.updateProfile(username, password, newFirstName, newLastName);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainer setTrainerActive(String username, String password, boolean active) {
        return trainerService.setActive(username, password, active);
    }

    public List<Training> getTrainerTrainings(String username, String password) {
        return trainerService.getTrainings(username, password);
    }

    public List<Training> getTrainerTrainingsByDate(String username, String password,
                                                    LocalDateTime from, LocalDateTime to) {
        return trainerService.findTrainingsByDateRange(username, password, from, to);
    }

    public List<Training> getTrainerTrainingsByTrainee(String username, String password, String traineeQuery) {
        return trainerService.findTrainingsByTraineeName(username, password, traineeQuery);
    }

    public List<Training> getTrainerTrainingsByType(String username, String password, String typeName) {
        return trainerService.findTrainingsByType(username, password, typeName);
    }

    public List<Trainer> findAllTrainers() {
        return trainerService.findAll();
    }

    //Training
    public Training addTraining(String trainerUsername, String trainerPassword,
                                String traineeUsername, String trainingName,
                                String trainingTypeName, LocalDateTime trainingDate,
                                int durationMinutes) {
        return trainingService.addTraining(trainerUsername, trainerPassword,
                traineeUsername, trainingName, trainingTypeName, trainingDate, durationMinutes);
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training updateTraining(Training training) {
        return trainingService.update(training);
    }

    public Optional<Training> findTrainingById(Long id) {
        return trainingService.findById(id);
    }

    public List<Training> findAllTrainings(Long id) {
        return trainingService.findAll();
    }

    public boolean deleteTraining(Long id) {
        return trainingService.deleteById(id);
    }
}
