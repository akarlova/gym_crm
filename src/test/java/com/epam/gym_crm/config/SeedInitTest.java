package com.epam.gym_crm.config;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.ITraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SeedInitTest {

    @Autowired
    @Qualifier("traineeStorage")
    Map<Long, Trainee> trainees;
    @Autowired
    @Qualifier("trainerStorage")
    Map<Long, Trainer> trainers;
    @Autowired
    @Qualifier("trainingStorage")
    Map<Long, Training> trainings;
    @Autowired
    ITraineeService traineeService;

@Test
    void dataIsSeeded() {
    assertEquals(3, trainees.size());
    assertEquals(4, trainers.size());
    assertEquals(3, trainings.size());
}
@Test
    void isIdCounterIncrementedWithRightId() {
    var trainee = new Trainee();
    trainee.setFirstName("Sansa");
    trainee.setLastName("Stark");
    var savedTrainee = traineeService.create(trainee);

    assertEquals(4, savedTrainee.getId());
}

}
