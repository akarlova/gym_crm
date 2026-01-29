package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.repository.ITrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TrainingTypeRepositoryImpl implements ITrainingTypeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        return entityManager.createQuery("""
                        select tt
                        from TrainingType tt
                        order by tt.name
                        """, TrainingType.class)
                .getResultList();
    }
}
