package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainee;
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
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {
    private ITraineeRepository traineeRepo;
    private ITrainerRepository trainerRepo;
    private IUsernameGenerator usernameGen;
    private IPasswordGenerator passwordGen;
    private IAuthService auth;

    private TraineeServiceImpl service;

    @BeforeEach
    void setup() {
        traineeRepo = mock(ITraineeRepository.class);
        trainerRepo = mock(ITrainerRepository.class);
        usernameGen = mock(IUsernameGenerator.class);
        passwordGen = mock(IPasswordGenerator.class);
        auth = mock(IAuthService.class);

        service = new TraineeServiceImpl(traineeRepo, trainerRepo, usernameGen, passwordGen, auth);
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

    private static Trainee trainee(String username, String pwd, boolean active) {
        Trainee t = new Trainee();
        t.setUser(user(username, pwd, active, "Arya", "Stark"));
        return t;
    }

    private static Trainer trainer(String username) {
        Trainer tr = new Trainer();
        tr.setUser(user(username, "pwd", true, "Jon", "Snow"));
        return tr;
    }


    @Test
    void create_generatesUsernameAndPassword_whenMissing() {

        Trainee arya = new Trainee();
        arya.setUser(user(null, null, true, "Arya", "Stark"));

        when(usernameGen.generate(any())).thenReturn("arya.stark");
        when(traineeRepo.findByUsername("arya.stark")).thenReturn(Optional.empty());
        when(passwordGen.generate()).thenReturn("ValarMorghulis!");
        when(traineeRepo.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Trainee saved = service.create(arya);

        // then
        assertEquals("Arya", saved.getUser().getFirstName()); // нормализация имени
        assertEquals("Stark", saved.getUser().getLastName());
        assertEquals("arya.stark", saved.getUser().getUsername());
        assertEquals("ValarMorghulis!", saved.getUser().getPassword());

        InOrder io = inOrder(usernameGen, traineeRepo, passwordGen);
        io.verify(usernameGen).generate(any());
        io.verify(traineeRepo).findByUsername("arya.stark");
        io.verify(passwordGen).generate();
        io.verify(traineeRepo).save(any(Trainee.class));
    }

    @Test
    void create_resolvesUsernameCollision_byAddingSuffix() {
        Trainee d = new Trainee();
        d.setUser(user(null, null, true, "Daenerys", "Targaryen"));

        when(usernameGen.generate(any())).thenReturn("daenerys.targaryen");

        when(traineeRepo.findByUsername("daenerys.targaryen"))
                .thenReturn(Optional.of(new Trainee()));

        when(traineeRepo.findByUsername("daenerys.targaryen1"))
                .thenReturn(Optional.empty());
        when(trainerRepo.findByUsername("daenerys.targaryen1"))
                .thenReturn(Optional.empty());
        when(passwordGen.generate()).thenReturn("Dracarys!");
        when(traineeRepo.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee saved = service.create(d);

        assertEquals("daenerys.targaryen1", saved.getUser().getUsername());
        assertEquals("Dracarys!", saved.getUser().getPassword());
    }

    @Test
    void getProfile_returnsTrainee_whenAuthOk() {
        when(auth.verifyTrainee("sansa.stark", "winterfell")).thenReturn(true);
        Trainee sansa = trainee("sansa.stark", "winterfell", true);
        when(traineeRepo.findByUsername("sansa.stark")).thenReturn(Optional.of(sansa));

        Trainee out = service.getProfile("sansa.stark", "winterfell");

        assertSame(sansa, out);
        verify(traineeRepo).findByUsername("sansa.stark");
    }

    @Test
    void getProfile_throws_whenAuthFails() {
        when(auth.verifyTrainee("bran.stark", "threeEyed")).thenReturn(false);
        assertThrows(RuntimeException.class,
                () -> service.getProfile("bran.stark", "threeEyed"));
        verifyNoInteractions(traineeRepo);
    }

    @Test
    void updateProfile_updatesOnlyProvidedFields() {
        when(auth.verifyTrainee("arya.stark", "needle")).thenReturn(true);
        Trainee arya = trainee("arya.stark", "needle", true);
        arya.setDateOfBirth(LocalDate.of(2000, 1, 1));
        arya.setAddress("Old address");

        when(traineeRepo.findByUsername("arya.stark")).thenReturn(Optional.of(arya));
        when(traineeRepo.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee updated = service.updateProfile(
                "arya.stark", "needle",
                "arya",       // firstName (нормализуется в "Arya")
                null,         // lastName не меняем
                LocalDate.of(2001, 2, 2),
                "King's Landing");

        assertEquals("Arya", updated.getUser().getFirstName());
        assertEquals("Stark", updated.getUser().getLastName()); // не менялся
        assertEquals(LocalDate.of(2001, 2, 2), updated.getDateOfBirth());
        assertEquals("King's Landing", updated.getAddress());
    }

    @Test
    void changePassword_happyPath() {
        when(auth.verifyTrainee("jon.snow", "ghost")).thenReturn(true);
        Trainee jon = trainee("jon.snow", "ghost", true);
        when(traineeRepo.findByUsername("jon.snow")).thenReturn(Optional.of(jon));

        service.changePassword("jon.snow", "ghost", "longclaw");
        assertEquals("longclaw", jon.getUser().getPassword());
        verify(traineeRepo).update(jon);
    }

    @Test
    void changePassword_throws_onBlankNewPassword() {
        when(auth.verifyTrainee("tyrion.lannister", "wine")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword("tyrion.lannister", "wine", " "));

        verify(traineeRepo, never()).update(any());
        verify(traineeRepo, never()).findByUsername(anyString());
    }

    @Test
    void setActive_updatesFlag() {
        when(auth.verifyTrainee("cersei.lannister", "casterly")).thenReturn(true);
        Trainee cersei = trainee("cersei.lannister", "casterly", true);
        when(traineeRepo.findByUsername("cersei.lannister")).thenReturn(Optional.of(cersei));
        when(traineeRepo.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee res = service.setActive("cersei.lannister", "casterly", false);
        assertFalse(res.getUser().isActive());
        verify(traineeRepo).update(cersei);
    }

    @Test
    void deleteByUsername_returnsTrue_whenExistsAndDeleted() {
        when(auth.verifyTrainee("jaime.lannister", "oathbreaker")).thenReturn(true);
        when(traineeRepo.findByUsername("jaime.lannister"))
                .thenReturn(Optional.of(trainee("jaime.lannister", "oathbreaker", true)));

        boolean ok = service.deleteByUsername("jaime.lannister", "oathbreaker");
        assertTrue(ok);
        verify(traineeRepo).deleteByUsername("jaime.lannister");
    }

    @Test
    void deleteByUsername_returnsFalse_whenNotFound() {
        when(auth.verifyTrainee("varys", "littleBirds")).thenReturn(true);
        when(traineeRepo.findByUsername("varys")).thenReturn(Optional.empty());

        boolean ok = service.deleteByUsername("varys", "littleBirds");
        assertFalse(ok);
        verify(traineeRepo, never()).deleteByUsername(anyString());
    }

    @Test
    void getTrainings_and_filters_delegateToRepo_whenAuthOk() {
        when(auth.verifyTrainee("brienne.tarth", "oath")).thenReturn(true);
        when(traineeRepo.findTrainings("brienne.tarth"))
                .thenReturn(List.of(new Training()));

        assertEquals(1, service.getTrainings("brienne.tarth", "oath").size());
        verify(traineeRepo).findTrainings("brienne.tarth");

        when(traineeRepo.findTrainingsByDateRange(eq("brienne.tarth"), any(), any()))
                .thenReturn(List.of(new Training(), new Training()));

        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();
        assertEquals(2, service.findTrainingsByDateRange("brienne.tarth", "oath", from, to).size());

        when(traineeRepo.findTrainingsByTrainerName("brienne.tarth", "jaime"))
                .thenReturn(List.of(new Training()));
        assertEquals(1, service.findTrainingsByTrainerName("brienne.tarth", "oath", "jaime").size());

        when(traineeRepo.findTrainingsByType("brienne.tarth", "strength"))
                .thenReturn(List.of());
        assertEquals(0, service.findTrainingsByType("brienne.tarth", "oath", "strength").size());
    }

    @Test
    void findNotAssignedTrainers_returnsList_whenAuthOk() {
        when(auth.verifyTrainee("theon.greyjoy", "reek")).thenReturn(true);
        when(traineeRepo.findNotAssignedTrainers("theon.greyjoy"))
                .thenReturn(List.of(trainer("sandor.clegane"), trainer("jorah.mormont")));

        List<Trainer> res = service.findNotAssignedTrainers("theon.greyjoy", "reek");
        assertEquals(2, res.size());
    }

    @Test
    void updateTrainers_replacesSet() {
        when(auth.verifyTrainee("samwell.tarly", "gilly")).thenReturn(true);

        Trainee sam = trainee("samwell.tarly", "gilly", true);
        sam.setTrainers(Set.of(trainer("old.trainer")));
        when(traineeRepo.findByUsername("samwell.tarly")).thenReturn(Optional.of(sam));

        Trainer jaqen = trainer("jaqen.hghar");
        when(trainerRepo.findByUsername("jaqen.hghar")).thenReturn(Optional.of(jaqen));

        when(traineeRepo.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee updated = service.updateTrainers("samwell.tarly", "gilly",
                List.of("jaqen.hghar"));

        assertEquals(1, updated.getTrainers().size());
        assertTrue(updated.getTrainers().stream().anyMatch(t -> "jaqen.hghar".equals(t.getUser().getUsername())));
        verify(trainerRepo).findByUsername("jaqen.hghar");
        verify(traineeRepo).update(sam);
    }
}
