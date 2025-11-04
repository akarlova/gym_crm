package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.repository.ITrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements ITrainingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Training create(Training entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public Training update(Training entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return entityManager.createQuery("""
                        select t
                        from Training t
                        order by t.trainingDate desc
                        """, Training.class)
                .getResultList();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        var entity = entityManager.find(Training.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> findTypeByName(String name) {
        String n = name == null ? "" : name.trim().toLowerCase();
        var list = entityManager.createQuery("""
            
                        select tt
            from TrainingType tt
            where lower(tt.name) = :n
            """, TrainingType.class)
                .setParameter("n", n)
                .getResultList();

        return list.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> findTypeById(Long id) {
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }
}