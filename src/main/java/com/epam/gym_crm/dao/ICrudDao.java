package com.epam.gym_crm.dao;

import java.util.List;
import java.util.Optional;

public interface ICrudDao<T, ID> {
    T create(T entity);

    T update(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);

    boolean existsById(ID id);
}
