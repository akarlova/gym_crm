package com.epam.gym_crm.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabasePingHealthIndicator implements HealthIndicator {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Health health() {
        try {
            Integer one = (Integer) entityManager.createNativeQuery("select 1").getSingleResult();
            return (one != null && one == 1)
                    ? Health.up().withDetail("dbPing","ok").build()
                    : Health.down().withDetail("dbPing","unexpected result").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("dbPing","failed").build();
        }
    }
}
