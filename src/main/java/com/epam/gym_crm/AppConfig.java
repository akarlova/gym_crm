package com.epam.gym_crm;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.epam.gym_crm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    //todo change on domain name
    @Bean(name = "traineeStorage")
    public Map<Long, Object> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainerStorage")
    public Map<Long, Object> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainingStorage")
    public Map<Long, Object> trainingStorage() {
        return new HashMap<>();
    }
}
