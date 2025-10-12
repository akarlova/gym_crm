package com.epam.gym_crm.repository;

import java.util.List;
import java.util.Optional;

public interface ICrudRepository <T, ID>{
    T create(T entity);              // persist
    T update(T entity);              // merge
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
}
