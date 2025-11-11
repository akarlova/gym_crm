package com.epam.gym_crm.health;

import com.epam.gym_crm.repository.ITrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {
    private final ITrainingTypeRepository repo;

    public TrainingTypesHealthIndicator(ITrainingTypeRepository repo) {
        this.repo = repo;
    }

    @Override
    public Health health() {
        int size = repo.findAll().size();
        return (size > 0)
                ? Health.up().withDetail("trainingTypesCount", size).build()
                : Health.outOfService().withDetail("trainingTypesCount", size).withDetail("hint", "seed training types").build();
    }
}
