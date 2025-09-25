package com.epam.gym_crm.dao.impl;

import com.epam.gym_crm.dao.TraineeDao;
import com.epam.gym_crm.domain.Trainee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTraineeDao implements TraineeDao {
    private final ConcurrentHashMap<Long, Trainee> storage;
    private final AtomicLong count = new AtomicLong(0);

    public InMemoryTraineeDao(@Qualifier("traineeStorage") ConcurrentHashMap<Long, Trainee> storage) {
        this.storage = storage;
    }

    @Override
    public Trainee create(Trainee entity) {
        long id = count.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Trainee update(Trainee entity) {
        Objects.requireNonNull(entity.getId(), "Id must not be null");
        if (!storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Such Trainee has not been found " + entity.getId());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
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
