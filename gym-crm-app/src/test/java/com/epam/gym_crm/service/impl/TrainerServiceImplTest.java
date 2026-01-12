package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.service.IAuthService;
import com.epam.gym_crm.util.IPasswordGenerator;
import com.epam.gym_crm.util.IUsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {
    @Mock
    ITrainerRepository trainerRepo;
    @Mock
    ITraineeRepository traineeRepo;
    @Mock
    IUsernameGenerator usernameGen;
    @Mock
    IPasswordGenerator passwordGen;
    @Mock
    IAuthService auth;

    TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TrainerServiceImpl(
                trainerRepo, traineeRepo, usernameGen, passwordGen, auth
        );
    }

    private static User user(String username, String pwd, boolean active, String first, String last) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(pwd);
        u.setActive(active);
        u.setFirstName(first);
        u.setLastName(last);
        return u;
    }

    private static Trainer trainer(String username, String pwd, boolean active) {
        Trainer t = new Trainer();
        t.setUser(user(username, pwd, active, "Arya", "Stark"));
        return t;
    }

    @Test
    void create_generatesUsernameAndPassword_whenMissing() {
        Trainer jon = new Trainer();
        jon.setUser(user(null, null, true, "jon", "snow")); // имена будут нормализованы

        when(usernameGen.generate(any())).thenReturn("jon.snow");
        when(traineeRepo.findByUsername("jon.snow")).thenReturn(Optional.empty());
        when(trainerRepo.findByUsername("jon.snow")).thenReturn(Optional.empty());
        when(passwordGen.generate()).thenReturn("Ghost!");
        when(trainerRepo.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer saved = service.create(jon);

        assertEquals("Jon", saved.getUser().getFirstName());
        assertEquals("Snow", saved.getUser().getLastName());
        assertEquals("jon.snow", saved.getUser().getUsername());
        assertEquals("Ghost!", saved.getUser().getPassword());
    }

    @Test
    void create_resolvesUsernameCollision_byAddingSuffix() {
        Trainer danny = new Trainer();
        danny.setUser(user(null, null, true, "Daenerys", "Targaryen"));

        when(usernameGen.generate(any())).thenReturn("daenerys.targaryen");
        when(traineeRepo.findByUsername("daenerys.targaryen"))
                .thenReturn(Optional.of(new com.epam.gym_crm.domain.Trainee()));
        when(traineeRepo.findByUsername("daenerys.targaryen1"))
                .thenReturn(Optional.empty());
        when(trainerRepo.findByUsername("daenerys.targaryen1"))
                .thenReturn(Optional.empty());

        when(passwordGen.generate()).thenReturn("Dracarys!");
        when(trainerRepo.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer saved = service.create(danny);

        assertEquals("daenerys.targaryen1", saved.getUser().getUsername());
        assertEquals("Dracarys!", saved.getUser().getPassword());
    }

    @Test
    void updateProfile_updatesNames_andNormalizes() {
        when(auth.verifyTrainer("sandor.clegane", "chicken")).thenReturn(true);
        Trainer sandor = trainer("sandor.clegane", "chicken", true);
        sandor.getUser().setFirstName("Sandor");
        sandor.getUser().setLastName("Clegane");

        when(trainerRepo.findByUsername("sandor.clegane"))
                .thenReturn(Optional.of(sandor));
        when(trainerRepo.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = service.updateProfile("sandor.clegane", "chicken",
                "sAnDoR", "cLeGaNe");

        assertEquals("Sandor", updated.getUser().getFirstName());
        assertEquals("Clegane", updated.getUser().getLastName());
        verify(trainerRepo).update(sandor);
    }

    @Test
    void updateProfile_throws_onAuthFail() {
        when(auth.verifyTrainer("varys", "littlebirds")).thenReturn(false);
        assertThrows(RuntimeException.class,
                () -> service.updateProfile("varys", "littlebirds", "Lord", "Varys"));
        verify(trainerRepo, never()).update(any());
    }

    @Test
    void getProfile_ok() {
        when(auth.verifyTrainer("brienne.tarth", "oath")).thenReturn(true);
        Trainer brienne = trainer("brienne.tarth", "oath", true);
        when(trainerRepo.findByUsername("brienne.tarth"))
                .thenReturn(Optional.of(brienne));

        Trainer got = service.getProfile("brienne.tarth", "oath");
        assertSame(brienne, got);
    }

    @Test
    void changePassword_ok() {
        when(auth.verifyTrainer("jaime.lannister", "gold")).thenReturn(true);
        Trainer jaime = trainer("jaime.lannister", "gold", true);
        when(trainerRepo.findByUsername("jaime.lannister"))
                .thenReturn(Optional.of(jaime));
        when(trainerRepo.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changePassword("jaime.lannister", "gold", "kingslayer");

        assertEquals("kingslayer", jaime.getUser().getPassword());
        verify(trainerRepo).update(jaime);
    }

    @Test
    void changePassword_throws_onBlankNewPassword() {
        when(auth.verifyTrainer("tyrion.lannister", "wine")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword("tyrion.lannister", "wine", " "));
        verify(trainerRepo, never()).update(any());
        verify(trainerRepo, never()).findByUsername(anyString());
    }

    @Test
    void setActive_ok() {
        when(auth.verifyTrainer("jorah.mormont", "khaleesi")).thenReturn(true);
        Trainer jorah = trainer("jorah.mormont", "khaleesi", false);
        when(trainerRepo.findByUsername("jorah.mormont"))
                .thenReturn(Optional.of(jorah));
        when(trainerRepo.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = service.setActive("jorah.mormont", "khaleesi", true);

        assertTrue(updated.getUser().isActive());
        verify(trainerRepo).update(jorah);
    }

    @Test
    void getTrainings_ok() {
        when(auth.verifyTrainer("oberin.martell", "viper")).thenReturn(true);
        List<Training> list = List.of(new Training(), new Training());
        when(trainerRepo.findTrainings("oberin.martell")).thenReturn(list);

        List<Training> res = service.getTrainings("oberin.martell", "viper");
        assertEquals(2, res.size());
    }

    @Test
    void findTrainingsByDateRange_validatesArgs() {
        when(auth.verifyTrainer("robb.stark", "wolf")).thenReturn(true);
        LocalDateTime from = LocalDateTime.now().plusDays(1);
        LocalDateTime to = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class,
                () -> service.findTrainingsByDateRange("robb.stark", "wolf", from, to));
        verify(trainerRepo, never()).findTrainingsByDateRange(anyString(), any(), any());
    }

    @Test
    void findTrainingsByTraineeName_ok() {
        when(auth.verifyTrainer("barristan.selm y", "whitecloak")).thenReturn(true);
        when(trainerRepo.findTrainingsByTraineeName("barristan.selm y", "arya"))
                .thenReturn(List.of(new Training()));

        List<Training> res = service.findTrainingsByTraineeName("barristan.selm y", "whitecloak", "arya");
        assertEquals(1, res.size());
    }

    @Test
    void findTrainingsByType_throws_onBlank() {
        when(auth.verifyTrainer("melisandre", "light")).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> service.findTrainingsByType("melisandre", "light", "  "));
        verify(trainerRepo, never()).findTrainingsByType(anyString(), anyString());
    }

    @Test
    void findAll_delegates() {
        when(trainerRepo.findAll()).thenReturn(List.of(new Trainer(), new Trainer(), new Trainer()));
        assertEquals(3, service.findAll().size());
    }
}
