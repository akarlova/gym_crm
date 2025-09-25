package com.epam.gym_crm.dao.impl;

import com.epam.gym_crm.dao.TrainerDao;
import com.epam.gym_crm.domain.Trainer;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTrainerDao implements TrainerDao {

    private final ConcurrentHashMap<Long, Trainer> storage;
    private final AtomicLong count = new AtomicLong(0);

    public InMemoryTrainerDao(@Qualifier("trainerStorage") ConcurrentHashMap<Long, Trainer> storage) {
        this.storage = storage;
    }

    @Override
    public Trainer create(Trainer entity) {
        long id = count.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Trainer update(Trainer entity) {
        Objects.requireNonNull(entity.getId(), "Id must not be null");
        if (!storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Such Trainer has not been found " + entity.getId());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
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
