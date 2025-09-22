package com.epam.gym_crm;

import com.epam.gym_crm.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            log.info("Spring context up. Beans: {}", context.getBeanDefinitionCount());
        }
    }
}
