package com.epam.gym_crm.config;


import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "com.epam.gym_crm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean(name = "traineeStorage")
    public ConcurrentHashMap<Long, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "trainerStorage")
    public ConcurrentHashMap<Long, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "trainingStorage")
    public ConcurrentHashMap<Long, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }
}
