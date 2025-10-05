package com.epam.gym_crm.service;

import com.epam.gym_crm.config.AppConfig;
import com.epam.gym_crm.domain.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(AppConfig.class)
public class TrainerServiceTest {
    private final ITrainerService trainerService;
    private final Map<Long, Trainer> storage;

    @Autowired
    public TrainerServiceTest(ITrainerService trainerService,
                              @Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.trainerService = trainerService;
        this.storage = storage;
    }

    @BeforeEach
    void clean() {
        storage.clear();
    }

    @Test
    void handleEqualNamesIntoUsernameTest(){
        var trainer = new Trainer();
        var trainer1 = new Trainer();
        var trainer2 = new Trainer();

        trainer.setFirstName("Tywin"); trainer.setLastName("Lannister");
        trainer1.setFirstName("tywin"); trainer1.setLastName("laNNistER");
        trainer2.setFirstName("Tywin"); trainer2.setLastName("LANNISTER");

        var savedTrainer = trainerService.create(trainer);
        var savedTrainer1 = trainerService.create(trainer1);
        var savedTrainer2 = trainerService.create(trainer2);

        assertEquals("Tywin.Lannister", savedTrainer.getUsername());
        assertEquals("Tywin.Lannister1", savedTrainer1.getUsername());
        assertEquals("Tywin.Lannister2", savedTrainer2.getUsername());

        assertEquals(3, storage.size());

    }
    @Test
    void normalizesNameToTitleCaseTest() {
        var trainer = new Trainer();
        trainer.setFirstName("ned");
        trainer.setLastName("StaRk");
        var savedTrainer = trainerService.create(trainer);

        assertEquals("Ned", savedTrainer.getFirstName());
        assertEquals("Stark", savedTrainer.getLastName());
        assertEquals("Ned.Stark", savedTrainer.getUsername());
    }
    @Test
    void generatePasswordOfLength10() {
        var trainer = new Trainer();
        trainer.setFirstName("Petyr");
        trainer.setLastName("Baelish");
        var savedTrainer = trainerService.create(trainer);

        assertEquals(10, savedTrainer.getPassword().length());
    }
}
