package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.repository.ITraineeRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements ITraineeRepository {

    @Override
    public Trainee save(Trainee trainee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            if (trainee.getId() == null) {
                session.persist(trainee);
            } else {
                trainee = session.merge(trainee);
            }
            transaction.commit();
            return trainee;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                select distinct tr
                from Trainee tr
                join fetch tr.user u
                left join fetch tr.trainers t
                left join fetch t.user tu
                left join fetch t.specialization ts
                where u.username = :username
                """, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    @Override
    public void deleteByUsername(String username) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Trainee trainee = session.createQuery("""
                            select tr
                            from Trainee tr
                            join tr.user u
                            where u.username = :username
                            """, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (trainee != null) {
                session.remove(trainee);
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Training> findTrainings(String traineeUsername) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct t
                            from Training t
                            join t.trainee tr
                            join tr.user u
                            join fetch t.trainer trn
                            join fetch trn.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", traineeUsername)
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByDateRange(String traineeUsername, LocalDateTime from, LocalDateTime to) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct t
                            from Training t
                            join t.trainee tr
                            join tr.user u
                            join fetch t.trainer trn
                            join fetch trn.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
                            and t.trainingDate >= :from
                            and t.trainingDate <= :to
                            order by t.trainingDate desc
                             """, Training.class)
                    .setParameter("username", traineeUsername)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByTrainerName(String traineeUsername, String trainerName) {
        String q = trainerName == null ? "" : trainerName.trim().toLowerCase();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct t
                            from Training t
                            join t.trainee tr
                            join tr.user u
                            join fetch t.trainer trn
                            join fetch trn.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
                              and (
                                   lower(u2.username) like :q
                                or lower(u2.firstName) like :q
                                or lower(u2.lastName)  like :q
                                or lower(concat(u2.firstName, ' ', u2.lastName)) like :q
                              )
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", traineeUsername)
                    .setParameter("q", "%" + q + "%")
                    .getResultList();
        }
    }

    @Override
    public List<Training> findTrainingsByType(String traineeUsername, String trainingTypeName) {
        String type = trainingTypeName.trim().toLowerCase();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select distinct t
                            from Training t
                            join t.trainee tr
                            join tr.user u
                            join fetch t.trainer trn
                            join fetch trn.user u2
                            join fetch t.trainingType tt
                            where u.username = :username
                              and lower(tt.name) = :type
                            order by t.trainingDate desc
                            """, Training.class)
                    .setParameter("username", traineeUsername)
                    .setParameter("type", type)
                    .getResultList();
        }
    }

    @Override
    public List<Trainer> findNotAssignedTrainers(String traineeUsername) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            select trn
                            from Trainer trn
                            join fetch trn.user u1
                            join fetch trn.specialization ts
                            where u1.active = true
                             and trn.id not in (
                              select trn2.id
                              from Trainee t
                              join t.trainers trn2
                              join t.user u2
                              where u2.username = :username
                                               )
                            """, Trainer.class)
                    .setParameter("username", traineeUsername)
                    .getResultList();
        }
    }

    //CRUD
    @Override
    public Trainee create(Trainee trainee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainee);
            transaction.commit();
            return trainee;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Trainee update(Trainee trainee) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Trainee merged = session.merge(trainee);
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
    public Optional<Trainee> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Trainee.class, id));
        }
    }

    @Override
    public List<Trainee> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Trainee", Trainee.class).getResultList();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Trainee trainee = session.find(Trainee.class, id);
            if (trainee != null) {
                session.remove(trainee);
            }
            transaction.commit();
            return trainee != null;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}

