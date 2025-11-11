package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.repository.ITrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements ITrainerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Trainer save(Trainer trainer) {
        return entityManager.merge(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsername(String username) {
        var list = entityManager.createQuery("""
                        select distinct trn
                        from Trainer trn
                        join fetch trn.user u
                        left join fetch trn.trainees t
                        left join fetch t.user tu
                        left join fetch trn.specialization ts
                        where u.username = :username
                        """, Trainer.class)
                .setParameter("username", username)
                .getResultList();

        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainings(String trainerUsername) {
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByDateRange(String trainerUsername,
                                                   LocalDateTime from, LocalDateTime to) {
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByTraineeName(String trainerUsername, String traineeName) {
        String q = traineeName == null ? "" : traineeName.trim().toLowerCase();
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByType(String trainerUsername, String trainingTypeName) {
        String type = trainingTypeName == null ? "" : trainingTypeName.trim().toLowerCase();
        return entityManager.createQuery("""
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

    //CRUD
    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        entityManager.persist(trainer);
        return trainer;
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        return entityManager.merge(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return entityManager.createQuery("select t from Trainer t", Trainer.class)
                .getResultList();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        var entity = entityManager.find(Trainer.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }
}

