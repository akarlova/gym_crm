package com.epam.gym_crm.facade;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.service.ITrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {
    @Mock
    ITraineeService traineeService;
    @Mock
    ITrainerService trainerService;
    @Mock
    ITrainingService trainingService;

    @InjectMocks
    GymFacade facade;

    // ---------- Trainee ----------
    @Test
    void createTrainee_delegatesToService_andReturnsResult() {
        Trainee arya = new Trainee();
        when(traineeService.create(arya)).thenReturn(arya);

        Trainee res = facade.createTrainee(arya);

        assertSame(arya, res);
        verify(traineeService).create(arya);
        verifyNoInteractions(trainerService, trainingService);
    }

    @Test
    void updateTraineeProfile_delegatesWithAllArgs() {
        LocalDate dob = LocalDate.of(2003, 3, 3);
        Trainee updated = new Trainee();
        when(traineeService.updateProfile("arya.stark", "needle", "Arya", "Stark", dob, "Winterfell"))
                .thenReturn(updated);

        Trainee res = facade.updateTraineeProfile("arya.stark", "needle", "Arya", "Stark", dob, "Winterfell");

        assertSame(updated, res);
        verify(traineeService).updateProfile("arya.stark", "needle", "Arya", "Stark", dob, "Winterfell");
        verifyNoInteractions(trainerService, trainingService);
    }

    @Test
    void getTraineeTrainingsByDate_delegates() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to   = LocalDateTime.now();
        List<Training> list = List.of(new Training());
        when(traineeService.findTrainingsByDateRange("arya.stark", "needle", from, to)).thenReturn(list);

        List<Training> res = facade.getTraineeTrainingsByDate("arya.stark", "needle", from, to);

        assertSame(list, res);
        verify(traineeService).findTrainingsByDateRange("arya.stark", "needle", from, to);
        verifyNoInteractions(trainerService, trainingService);
    }

    // ---------- Trainer ----------
    @Test
    void createTrainer_delegates() {
        Trainer jon = new Trainer();
        when(trainerService.create(jon)).thenReturn(jon);

        Trainer res = facade.createTrainer(jon);

        assertSame(jon, res);
        verify(trainerService).create(jon);
        verifyNoInteractions(traineeService, trainingService);
    }

    @Test
    void getTrainerTrainingsByTrainee_delegates() {
        List<Training> list = List.of(new Training());
        when(trainerService.findTrainingsByTraineeName("jon.snow", "ghost", "arya"))
                .thenReturn(list);

        List<Training> res = facade.getTrainerTrainingsByTrainee("jon.snow", "ghost", "arya");

        assertSame(list, res);
        verify(trainerService).findTrainingsByTraineeName("jon.snow", "ghost", "arya");
        verifyNoInteractions(traineeService, trainingService);
    }

    // ---------- Training ----------
    @Test
    void addTraining_delegates_allArgs() {
        Training t = new Training();
        LocalDateTime when = LocalDateTime.now().plusHours(1);
        when(trainingService.addTraining("jon.snow", "ghost",
                "arya.stark", "Sparring", "STRENGTH", when, 45))
                .thenReturn(t);

        Training res = facade.addTraining("jon.snow", "ghost",
                "arya.stark", "Sparring", "STRENGTH", when, 45);

        assertSame(t, res);
        verify(trainingService).addTraining("jon.snow", "ghost",
                "arya.stark", "Sparring", "STRENGTH", when, 45);
        verifyNoInteractions(traineeService, trainerService);
    }

    @Test
    void findTrainingById_delegates_andReturnsOptional() {
        Training t = new Training();
        when(trainingService.findById(7L)).thenReturn(Optional.of(t));

        Optional<Training> res = facade.findTrainingById(7L);

        assertTrue(res.isPresent());
        assertSame(t, res.get());
        verify(trainingService).findById(7L);
        verifyNoInteractions(traineeService, trainerService);
    }

    @Test
    void deleteTraining_delegates_andReturnsFlag() {
        when(trainingService.deleteById(99L)).thenReturn(true);

        boolean ok = facade.deleteTraining(99L);

        assertTrue(ok);
        verify(trainingService).deleteById(99L);
        verifyNoInteractions(traineeService, trainerService);
    }

    @Test
    void getTraineeProfile_propagatesExceptionFromService() {
        RuntimeException boom = new RuntimeException("auth failed");
        when(traineeService.getProfile("cersei", "wine")).thenThrow(boom);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> facade.getTraineeProfile("cersei", "wine"));
        assertSame(boom, ex);

        verify(traineeService).getProfile("cersei", "wine");
        verifyNoInteractions(trainerService, trainingService);
    }

}
