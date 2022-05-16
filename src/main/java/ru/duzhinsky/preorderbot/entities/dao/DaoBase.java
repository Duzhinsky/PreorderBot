package ru.duzhinsky.preorderbot.entities.dao;

import java.io.Serializable;
import java.util.List;

public interface DaoBase<T, Id extends Serializable> {
    void create(T entity);
    void update(T entity);
    T findById(Id id);
    void delete(T entity);
}
