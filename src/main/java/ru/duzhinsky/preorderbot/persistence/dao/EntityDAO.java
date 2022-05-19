package ru.duzhinsky.preorderbot.persistence.dao;

import java.io.Serializable;
import java.util.function.Consumer;

public interface EntityDAO<T, Id extends Serializable> {
    T find(Id id);
    void remove(T e);
    void persist(T e);
    void update(T e, Consumer<T>... updates);
    void close();
}
