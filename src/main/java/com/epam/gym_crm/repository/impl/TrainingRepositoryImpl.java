package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.repository.ITrainingRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements ITrainingRepository {
    @Override
    public Training create(Training entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        }
    }

    @Override
    public Training update(Training entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Training merged = session.merge(entity);
            transaction.commit();
            return merged;
        }
    }

    @Override
    public Optional<Training> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Training.class, id));
        }
    }

    @Override
    public List<Training> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                    from Training t
                    order by t.trainingDate desc
                    """, Training.class).getResultList();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Training training = session.find(Training.class, id);
            if (training != null) {
                session.remove(training);
            }
            transaction.commit();
            return training != null;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<TrainingType> findTypeByName(String name) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
                              from TrainingType tt
                              where lower(tt.name) = :n
                            """, TrainingType.class)
                    .setParameter("n", name.toLowerCase().trim())
                    .uniqueResultOptional();
        }
    }

    @Override
    public Optional<TrainingType> findTypeById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(s.find(TrainingType.class, id));
        }
    }
}
