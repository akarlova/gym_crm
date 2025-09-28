package com.epam.gym_crm.service;


import com.epam.gym_crm.config.AppConfig;
import com.epam.gym_crm.domain.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
public class TraineeServiceTest {
    private final TraineeService traineeService;
    private final Map<Long, Trainee> storage;

    @Autowired
    TraineeServiceTest(TraineeService traineeService,
                       @Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.traineeService = traineeService;
        this.storage = storage;
    }

//    @BeforeEach
//    void clean() {
//        storage.clear();
//    }

    @Test
    void handleTwoEqualNamesIntoUsernameTest() {
        var trainee = new Trainee();
        trainee.setFirstName("Jon");
        trainee.setLastName("Snow");

        var trainee1 = new Trainee();
        trainee1.setFirstName("jOn");
        trainee1.setLastName("SNOW");

        var savedTrainee = traineeService.create(trainee);
        var savedTrainee1 = traineeService.create(trainee1);

        assertEquals("Jon.Snow", savedTrainee.getUsername());
        assertEquals("Jon.Snow1", savedTrainee1.getUsername());
    }

    @Test
    void normalizesNameToTitleCaseTest() {
        var trainee = new Trainee();
        trainee.setFirstName("daEnerYS");
        trainee.setLastName("tARGAryEN");
        var savedTrainee = traineeService.create(trainee);

        assertEquals("Daenerys", savedTrainee.getFirstName());
        assertEquals("Targaryen", savedTrainee.getLastName());
        assertEquals("Daenerys.Targaryen", savedTrainee.getUsername());
    }

    @Test
    void generatePasswordOfLength10() {
        var trainee = new Trainee();
        trainee.setFirstName("Robb");
        trainee.setLastName("Stark");
        var savedTrainee = traineeService.create(trainee);

        assertEquals(10, savedTrainee.getPassword().length());
    }
}
