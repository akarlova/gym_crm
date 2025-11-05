package com.epam.gym_crm.health;

import com.epam.gym_crm.repository.ITrainerRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrainersHealthIndicator implements HealthIndicator {
    private final ITrainerRepository repo;

    public ActiveTrainersHealthIndicator(ITrainerRepository repo) {
        this.repo = repo;
    }
    @Override
    public Health health() {
        long count = repo.findAll().stream()
                .filter(t -> t.getUser() != null && Boolean.TRUE.equals(t.getUser().isActive()))
                .count();

        return (count > 0)
                ? Health.up().withDetail("activeTrainers", count).build()
                : Health.down().withDetail("activeTrainers", count).build();
    }
}
