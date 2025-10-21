package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    ITraineeRepository traineeRepo;
    @Mock
    ITrainerRepository trainerRepo;

    @InjectMocks
    AuthServiceImpl authService;

    private static Trainee trainee(String username, String password, boolean active) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setActive(active);
        Trainee t = new Trainee();
        t.setUser(u);
        return t;
    }

    private static Trainer trainer(String username, String password, boolean active) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setActive(active);
        Trainer t = new Trainer();
        t.setUser(u);
        return t;
    }

    @Nested
    @DisplayName("verifyTrainee")
    class VerifyTrainee {

        @Test
        void returnsTrue_whenUserExistsActive_andPasswordMatches() {
            when(traineeRepo.findByUsername("alice"))
                    .thenReturn(Optional.of(trainee("alice", "p@ss", true)));

            assertTrue(authService.verifyTrainee("alice", "p@ss"));
            verify(traineeRepo).findByUsername("alice");
            verifyNoInteractions(trainerRepo);
        }

        @Test
        void returnsFalse_whenPasswordDoesNotMatch() {
            when(traineeRepo.findByUsername("alice"))
                    .thenReturn(Optional.of(trainee("alice", "p@ss", true)));

            assertFalse(authService.verifyTrainee("alice", "wrong"));
        }

        @Test
        void returnsFalse_whenUserIsInactive() {
            when(traineeRepo.findByUsername("alice"))
                    .thenReturn(Optional.of(trainee("alice", "p@ss", false)));

            assertFalse(authService.verifyTrainee("alice", "p@ss"));
        }

        @Test
        void returnsFalse_whenUserNotFound() {
            when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

            assertFalse(authService.verifyTrainee("unknown", "any"));
        }

        @Test
        void returnsFalse_whenArgsBlank() {
            assertFalse(authService.verifyTrainee(null, "x"));
            assertFalse(authService.verifyTrainee("  ", "x"));
            assertFalse(authService.verifyTrainee("alice", null));
            assertFalse(authService.verifyTrainee("alice", "  "));
            verifyNoInteractions(traineeRepo, trainerRepo);
        }
    }

    @Nested
    @DisplayName("verifyTrainer")
    class VerifyTrainer {

        @Test
        void returnsTrue_whenUserExistsActive_andPasswordMatches() {
            when(trainerRepo.findByUsername("john"))
                    .thenReturn(Optional.of(trainer("john", "123", true)));

            assertTrue(authService.verifyTrainer("john", "123"));
            verify(trainerRepo).findByUsername("john");
            verifyNoInteractions(traineeRepo);
        }

        @Test
        void returnsFalse_whenPasswordDoesNotMatch() {
            when(trainerRepo.findByUsername("john"))
                    .thenReturn(Optional.of(trainer("john", "123", true)));

            assertFalse(authService.verifyTrainer("john", "xxx"));
        }

        @Test
        void returnsFalse_whenUserIsInactive() {
            when(trainerRepo.findByUsername("john"))
                    .thenReturn(Optional.of(trainer("john", "123", false)));

            assertFalse(authService.verifyTrainer("john", "123"));
        }

        @Test
        void returnsFalse_whenUserNotFound() {
            when(trainerRepo.findByUsername("nobody")).thenReturn(Optional.empty());

            assertFalse(authService.verifyTrainer("nobody", "x"));
        }

        @Test
        void returnsFalse_whenArgsBlank() {
            assertFalse(authService.verifyTrainer(null, "x"));
            assertFalse(authService.verifyTrainer("  ", "x"));
            assertFalse(authService.verifyTrainer("john", null));
            assertFalse(authService.verifyTrainer("john", "  "));
            verifyNoInteractions(trainerRepo, traineeRepo);
        }
    }
}
