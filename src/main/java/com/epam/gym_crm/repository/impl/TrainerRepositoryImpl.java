package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.repository.ITrainerRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements ITrainerRepository {

    @Override
    public Trainer save(Trainer trainer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Trainer merged = session.merge(trainer);
            transaction.commit();
            return merged;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct trn
                            from Trainer trn
                            join fetch trn.user u
                            left join fetch trn.trainees t
                            left join fetch t.user tu
                            left join fetch trn.specialization ts
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
                            select distinct t
                            from Training t
                            join t.trainer trn
                            join trn.user u
                            join fetch t.trainee tr
                            join fetch tr.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
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
                            select distinct t
                            from Training t
                            join t.trainer trn
                            join trn.user u
                            join fetch t.trainee tr
                            join fetch tr.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
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
                            select distinct t
                            from Training t
                            join t.trainer trn
                            join trn.user u
                            join fetch t.trainee tr
                            join fetch tr.user u2
                            join fetch t.trainingType tt
                            where u.username = :trainerUsername
                              and (
                                   lower(u2.username) like :q
                                or lower(u2.firstName) like :q
                                or lower(u2.lastName)  like :q
                                or lower(concat(u2.firstName, ' ', u2.lastName)) like :q
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
        String type = trainingTypeName.trim().toLowerCase();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct t
                            from Training t
                            join t.trainer trn
                            join trn.user u
                            join fetch t.trainee tr
                            join fetch tr.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
                              and lower(tt.name) = :type
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
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainer);
            transaction.commit();
            return trainer;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Trainer update(Trainer trainer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Trainer merged = session.merge(trainer);
            transaction.commit();
            return merged;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
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

