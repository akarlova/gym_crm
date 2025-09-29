package com.epam.gym_crm.config;


import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "com.epam.gym_crm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

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
