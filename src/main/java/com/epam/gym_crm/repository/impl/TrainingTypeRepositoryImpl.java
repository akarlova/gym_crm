package com.epam.gym_crm.repository.impl;

import com.epam.gym_crm.config.HibernateUtil;
import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.repository.ITrainingTypeRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingTypeRepositoryImpl implements ITrainingTypeRepository {
    @Override
    public List<TrainingType> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from TrainingType order by name", TrainingType.class)
                    .getResultList();
        }
    }
}
