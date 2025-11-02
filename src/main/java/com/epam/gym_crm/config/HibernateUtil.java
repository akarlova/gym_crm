package com.epam.gym_crm.config;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Component
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public HibernateUtil(EntityManagerFactory emf) {
        HibernateUtil.sessionFactory = emf.unwrap(SessionFactory.class);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

