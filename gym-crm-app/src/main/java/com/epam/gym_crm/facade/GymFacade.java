package com.epam.gym_crm.facade;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.service.ITrainingService;
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

    public Trainee getTraineeProfile(String username) {
        return traineeService.getProfile(username);
    }


    public Trainee updateTraineeProfile(String username,
                                        String newFirstName, String newLastName,
                                        LocalDate newDateOfBirth, String newAddress) {
        return traineeService.updateProfile(username, newFirstName,
                newLastName, newDateOfBirth, newAddress);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee setTraineeActive(String username, boolean isActive) {
        return traineeService.setActive(username, isActive);
    }

    public List<Training> getTraineeTrainings(String username) {
        return traineeService.getTrainings(username);
    }

    public List<Training> getTraineeTrainingsByDate(String username,
                                                    LocalDateTime from, LocalDateTime to) {
        return traineeService.findTrainingsByDateRange(username, from, to);
    }

    public List<Training> getTraineeTrainingsByTrainer(String username,String trainerQuery) {
        return traineeService.findTrainingsByTrainerName(username, trainerQuery);
    }

    public List<Training> getTraineeTrainingsByType(String username, String typeName) {
        return traineeService.findTrainingsByType(username, typeName);
    }


    public List<Trainee> findAllTrainees() {
        return traineeService.findAll();
    }

    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        return traineeService.findNotAssignedTrainers(username);
    }

    public Trainee updateTraineeTrainers(String username,  List<String> trainerUsernames) {
        return traineeService.updateTrainers(username, trainerUsernames);
    }

    public boolean deleteTraineeByUsername(String username) {
        return traineeService.deleteByUsername(username);
    }

    //Trainer
    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer getTrainerProfile(String username) {
        return trainerService.getProfile(username);
    }

    public Trainer updateTrainerProfile(String username,
                                        String newFirstName, String newLastName) {
        return trainerService.updateProfile(username, newFirstName, newLastName);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainer setTrainerActive(String username, boolean active) {
        return trainerService.setActive(username, active);
    }

    public List<Training> getTrainerTrainings(String username) {
        return trainerService.getTrainings(username);
    }

    public List<Training> getTrainerTrainingsByDate(String username,
                                                    LocalDateTime from, LocalDateTime to) {
        return trainerService.findTrainingsByDateRange(username, from, to);
    }

    public List<Training> getTrainerTrainingsByTrainee(String username, String password, String traineeQuery) {
        return trainerService.findTrainingsByTraineeName(username, traineeQuery);
    }

    public List<Training> getTrainerTrainingsByType(String username, String typeName) {
        return trainerService.findTrainingsByType(username,  typeName);
    }

    public List<Trainer> findAllTrainers() {
        return trainerService.findAll();
    }

    //Training
    public Training addTraining(String trainerUsername,
                                String traineeUsername, String trainingName,
                                String trainingTypeName, LocalDateTime trainingDate,
                                int durationMinutes) {
        return trainingService.addTraining(trainerUsername,
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
