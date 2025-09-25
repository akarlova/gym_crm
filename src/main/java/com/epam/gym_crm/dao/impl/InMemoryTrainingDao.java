package com.epam.gym_crm.dao.impl;

import com.epam.gym_crm.dao.TrainingDao;
import com.epam.gym_crm.domain.Training;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTrainingDao implements TrainingDao {
    private final ConcurrentHashMap<Long, Training> storage;
    private final AtomicLong count = new AtomicLong(0);

    public InMemoryTrainingDao(@Qualifier("trainingStorage") ConcurrentHashMap<Long, Training> storage) {
        this.storage = storage;
    }

    @Override
    public Training create(Training entity) {
        long id = count.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Training update(Training entity) {
        Objects.requireNonNull(entity.getId(), "Id must not be null");
        if (!storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Such Training has not been found " + entity.getId());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
