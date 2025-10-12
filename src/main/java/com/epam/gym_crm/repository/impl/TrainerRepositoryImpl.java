package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.repository.ITrainerRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TrainerRepositoryImpl implements ITrainerRepository {

    @Override
    public Trainer save(Trainer trainer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (trainer.getId() == null) {
                session.persist(trainer);
            } else {
                trainer = session.merge(trainer);
            }
            transaction.commit();
            return trainer;
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select trn
                            from Trainer trn
                            join fetch trn.user u
                            where u.username = :username
                            """, Trainer.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Training> findTrainings(String trainerUsername) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select t
                            from Training t
                            where t.trainer.user.username = :username
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", trainerUsername)
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByDateRange(String trainerUsername, LocalDateTime from, LocalDateTime to) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select t
                            from Training t
                            where t.trainer.user.username = :username
                              and t.trainingDate >= :from
                              and t.trainingDate <= :to
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", trainerUsername)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByTraineeName(String trainerUsername, String traineeName) {
        String q = traineeName == null ? "" : traineeName.trim().toLowerCase();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select t
                            from Training t
                            where t.trainer.user.username = :trainerUsername
                              and (
                                  lower(t.trainee.user.username) like :q
                               or lower(t.trainee.user.firstName) like :q
                               or lower(t.trainee.user.lastName)  like :q
                               or lower(concat(t.trainee.user.firstName, ' ', t.trainee.user.lastName)) like :q
                              )
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("trainerUsername", trainerUsername)
                    .setParameter("q", "%" + q + "%")
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByType(String trainerUsername, String trainingTypeName) {
        String type = trainingTypeName.trim().toUpperCase();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select t
                            from Training t
                            where t.trainer.user.username = :username
                              and t.trainingType.name = :type
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", trainerUsername)
                    .setParameter("type", type)
                    .getResultList();
        }
    }

    //CRUD
    @Override
    public Trainer create(Trainer trainer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(trainer);
            transaction.commit();
            return trainer;
        }
    }

    @Override
    public Trainer update(Trainer trainer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Trainer merged = session.merge(trainer);
            transaction.commit();
            return merged;
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Trainer.class, id));
        }
    }

    @Override
    public List<Trainer> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Trainer", Trainer.class).getResultList();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Trainer trainer = session.find(Trainer.class, id);
            if (trainer != null) {
                session.remove(trainer);
            }
            transaction.commit();
            return trainer != null;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}

