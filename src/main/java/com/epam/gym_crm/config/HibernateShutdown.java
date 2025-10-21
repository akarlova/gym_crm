package com.epam.gym_crm.config;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

import static com.epam.gym_crm.config.HibernateUtil.getSessionFactory;

@Component
public class HibernateShutdown {
    @PreDestroy
    public void close() {
        getSessionFactory().close();
    }
}
