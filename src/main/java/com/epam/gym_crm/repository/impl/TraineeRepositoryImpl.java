package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.repository.ITraineeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements ITraineeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Trainee save(Trainee trainee) {
        if (trainee.getId() == null) {
            entityManager.persist(trainee);
            return trainee;
        } else {
            return entityManager.merge(trainee);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        var list = entityManager.createQuery("""
                        select distinct tr
                        from Trainee tr
                        join fetch tr.user u
                        left join fetch tr.trainers t
                        left join fetch t.user tu
                        left join fetch t.specialization ts
                        where u.username = :username
                        """, Trainee.class)
                .setParameter("username", username)
                .getResultList();

        return list.stream().findFirst();
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        var trainee = entityManager.createQuery("""
                        select tr
                        from Trainee tr
                        join tr.user u
                        where u.username = :username
                        """, Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (trainee != null) {
            entityManager.remove(trainee);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainings(String traineeUsername) {
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByDateRange(String traineeUsername, LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            var tmp = from;
            from = to;
            to = tmp;
        }
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByTrainerName(String traineeUsername, String trainerName) {
        String q = (trainerName == null ? "" : trainerName.trim().toLowerCase());
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> findTrainingsByType(String traineeUsername, String trainingTypeName) {
        String type = trainingTypeName == null ? "" : trainingTypeName.trim().toLowerCase();
        return entityManager.createQuery("""
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

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findNotAssignedTrainers(String traineeUsername) {
        return entityManager.createQuery("""
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

    //CRUD
    @Override
    @Transactional
    public Trainee create(Trainee trainee) {
        entityManager.persist(trainee);
        return trainee;
    }

    @Override
    @Transactional
    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        return entityManager.createQuery("select t from Trainee t", Trainee.class)
                .getResultList();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        var entity = entityManager.find(Trainee.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }
}

