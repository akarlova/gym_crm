package com.epam.gym_crm.config;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import static com.epam.gym_crm.config.HibernateUtil.getSessionFactory;

@Component
public class HibernateShutdown {
    @PreDestroy
    public void close() {
        getSessionFactory().close();
    }
}
