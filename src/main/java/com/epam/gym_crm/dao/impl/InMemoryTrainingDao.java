//package com.epam.gym_crm.dao.impl;
//
//import com.epam.gym_crm.dao.ITrainingDao;
//import com.epam.gym_crm.domain.Training;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Repository
//public class InMemoryTrainingDao implements ITrainingDao {
//    private final ConcurrentHashMap<Long, Training> storage;
//    private final AtomicLong count = new AtomicLong(0);
//
//    public InMemoryTrainingDao(@Qualifier("trainingStorage") ConcurrentHashMap<Long, Training> storage) {
//        this.storage = storage;
//        long start = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
//        this.count.set(start);
//    }
//
//    @Override
//    public Training create(Training entity) {
//        if (entity.getId() != null) {
//            throw new IllegalArgumentException("id must be null on create");
//        }
//        long id = count.incrementAndGet();
//        entity.setId(id);
//        storage.put(id, entity);
//        return entity;
//    }
//
//    @Override
//    public Training update(Training entity) {
//        Objects.requireNonNull(entity.getId(), "Id must not be null");
//        if (!storage.containsKey(entity.getId())) {
//            throw new NoSuchElementException("Such Training has not been found " + entity.getId());
//        }
//        storage.put(entity.getId(), entity);
//        return entity;
//    }
//
//    @Override
//    public Optional<Training> findById(Long id) {
//        return Optional.ofNullable(storage.get(id));
//    }
//
//    @Override
//    public List<Training> findAll() {
//        return new ArrayList<>(storage.values());
//    }
//
//    @Override
//    public boolean deleteById(Long id) {
//        return storage.remove(id) != null;
//    }
//
////    @Override
//    public boolean existsById(Long id) {
//        return storage.containsKey(id);
//    }
//}
