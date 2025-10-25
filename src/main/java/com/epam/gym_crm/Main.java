package com.epam.gym_crm;

import com.epam.gym_crm.config.AppConfig;
import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.repository.ITrainingRepository;
import com.epam.gym_crm.repository.impl.TraineeRepositoryImpl;
import com.epam.gym_crm.repository.impl.TrainerRepositoryImpl;
import com.epam.gym_crm.repository.impl.TrainingRepositoryImpl;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.service.ITrainingService;
import com.epam.gym_crm.service.impl.AuthServiceImpl;
import com.epam.gym_crm.service.impl.TraineeServiceImpl;
import com.epam.gym_crm.service.impl.TrainerServiceImpl;
import com.epam.gym_crm.service.impl.TrainingServiceImpl;
import com.epam.gym_crm.util.impl.BasePasswordGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.apache.catalina.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        tomcat.addWebapp(contextPath, docBase);

        log.info("Starting Tomcat on port 8080...");
        tomcat.start();
        tomcat.getServer().await();
    }
}