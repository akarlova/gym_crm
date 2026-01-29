//package com.epam.gym_crm.service.impl;
//
//import com.epam.gym_crm.domain.Trainee;
//import com.epam.gym_crm.domain.Trainer;
//import com.epam.gym_crm.domain.Training;
//import com.epam.gym_crm.domain.TrainingType;
//import com.epam.gym_crm.domain.User;
//import com.epam.gym_crm.repository.ITraineeRepository;
//import com.epam.gym_crm.repository.ITrainerRepository;
//import com.epam.gym_crm.repository.ITrainingRepository;
//import com.epam.gym_crm.service.IAuthService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class TrainingServiceImplTest {
//    @Mock
//    ITrainingRepository trainingRepo;
//    @Mock
//    ITrainerRepository trainerRepo;
//    @Mock
//    ITraineeRepository traineeRepo;
//    @Mock
//    IAuthService auth;
//
//    TrainingServiceImpl service;
//
//    @BeforeEach
//    void setup() {
//        service = new TrainingServiceImpl(trainingRepo, trainerRepo, traineeRepo, auth);
//    }
//
//    private static User user(String username) {
//        User u = new User();
//        u.setUsername(username);
//        u.setPassword("pwd");
//        u.setActive(true);
//        u.setFirstName("Fn");
//        u.setLastName("Ln");
//        return u;
//    }
//    private static Trainer trainerMock(long id, String username) {
//        Trainer tr = mock(Trainer.class);
//        when(tr.getId()).thenReturn(id);
//        return tr;
//    }
//
//    private static Trainee traineeWithTrainers(String username, Set<Trainer> trainers) {
//        Trainee t = new Trainee();
//        t.setUser(user(username));
//        t.setTrainers(trainers);
//        return t;
//    }
//    private static TrainingType type(String name) {
//        TrainingType tt = new TrainingType();
//        tt.setName(name);
//        return tt;
//    }
//
//    @Test
//    void addTraining_ok_whenTrainerAssignedAndAuthOk() {
//
//        String trainerU = "jon.snow";
//        String traineeU = "arya.stark";
//
//        when(auth.verifyTrainer(trainerU, "ghost")).thenReturn(true);
//
//        Trainer jon  = trainerMock(10L, trainerU);
//        Trainee arya = traineeWithTrainers(traineeU, Set.of(jon));
//
//        when(trainerRepo.findByUsername(trainerU)).thenReturn(Optional.of(jon));
//        when(traineeRepo.findByUsername(traineeU)).thenReturn(Optional.of(arya));
//        when(trainingRepo.findTypeByName("STRENGTH")).thenReturn(Optional.of(type("STRENGTH")));
//        when(trainingRepo.create(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        LocalDateTime when = LocalDateTime.now().plusHours(1);
//
//        // when
//        Training created = service.addTraining(
//                trainerU, "ghost",
//                traineeU,
//                "Evening practice",
//                "STRENGTH",
//                when,
//                45
//        );
//
//        // then
//        assertEquals(jon, created.getTrainer());
//        assertEquals(arya, created.getTrainee());
//        assertEquals("Evening practice", created.getTrainingName());
//        assertEquals(45, created.getTrainingDuration());
//        assertEquals("STRENGTH", created.getTrainingType().getName());
//        assertEquals(when, created.getTrainingDate());
//
//        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
//        verify(trainingRepo).create(captor.capture());
//        assertEquals("Evening practice", captor.getValue().getTrainingName());
//    }
//
//    @Test
//    void addTraining_throws_whenAuthFails() {
//        when(auth.verifyTrainer("cersei.lannister", "throne")).thenReturn(false);
//
//        assertThrows(RuntimeException.class, () ->
//                service.addTraining("cersei.lannister", "throne",
//                        "jaime.lannister", "Kingsguard drill", "STRENGTH",
//                        LocalDateTime.now(), 30));
//
//        verifyNoInteractions(trainerRepo, traineeRepo, trainingRepo);
//    }
//
//    @Test
//    void addTraining_throws_whenTrainerNotAssignedToTrainee() {
//        when(auth.verifyTrainer("bronn", "gold")).thenReturn(true);
//
//        Trainer bronn = new Trainer();
//        Trainee tyrion = traineeWithTrainers("tyrion.lannister", Set.of());
//
//        when(trainerRepo.findByUsername("bronn")).thenReturn(Optional.of(bronn));
//        when(traineeRepo.findByUsername("tyrion.lannister")).thenReturn(Optional.of(tyrion));
//
//        assertThrows(RuntimeException.class, () ->
//                service.addTraining("bronn", "gold",
//                        "tyrion.lannister", "Crossbow 101", "STRENGTH",
//                        LocalDateTime.now(), 50));
//
//        verify(trainingRepo, never()).create(any());
//    }
//
//    @Test
//    void addTraining_throws_onInvalidArgs() {
//        when(auth.verifyTrainer("the.hound", "chicken")).thenReturn(true);
//        // trainee username blank
//        assertThrows(IllegalArgumentException.class, () ->
//                service.addTraining("the.hound", "chicken",
//                        "  ", "Name", "STRENGTH", LocalDateTime.now(), 10));
//        // name blank
//        assertThrows(IllegalArgumentException.class, () ->
//                service.addTraining("the.hound", "chicken",
//                        "arya.stark", "  ", "STRENGTH", LocalDateTime.now(), 10));
//        // type blank
//        assertThrows(IllegalArgumentException.class, () ->
//                service.addTraining("the.hound", "chicken",
//                        "arya.stark", "Spar", "  ", LocalDateTime.now(), 10));
//        // date null
//        assertThrows(IllegalArgumentException.class, () ->
//                service.addTraining("the.hound", "chicken",
//                        "arya.stark", "Spar", "STRENGTH", null, 10));
//        // duration <= 0
//        assertThrows(IllegalArgumentException.class, () ->
//                service.addTraining("the.hound", "chicken",
//                        "arya.stark", "Spar", "STRENGTH", LocalDateTime.now(), 0));
//
//        verifyNoInteractions(trainingRepo);
//    }
//
//    @Test
//    void addTraining_throws_whenTypeNotFound() {
//        when(auth.verifyTrainer("jaime.lannister", "gold")).thenReturn(true);
//        Trainer jaime = trainerMock(7L, "jaime.lannister");
//        Trainee brienne = traineeWithTrainers("brienne.tarth", Set.of(jaime));
//
//        when(trainerRepo.findByUsername("jaime.lannister")).thenReturn(Optional.of(jaime));
//        when(traineeRepo.findByUsername("brienne.tarth")).thenReturn(Optional.of(brienne));
//        when(trainingRepo.findTypeByName("HONOR")).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () ->
//                service.addTraining("jaime.lannister", "gold",
//                        "brienne.tarth", "Sparring", "HONOR",
//                        LocalDateTime.now(), 30));
//
//        verify(trainingRepo, never()).create(any());
//    }
//}
