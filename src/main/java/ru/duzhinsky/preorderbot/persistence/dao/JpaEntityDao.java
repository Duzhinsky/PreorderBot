package ru.duzhinsky.preorderbot.persistence.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Consumer;

public class JpaEntityDao<T, Id extends Serializable> implements EntityDao<T, Id> {
    private final EntityManager entityManager;
    private final Class<T> clazz;

    public JpaEntityDao(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    @Override
    public T find(Id id) {
        return entityManager.find(clazz, id);
    }

    @Override
    public void remove(T e) {
        beginTransaction();
        entityManager.remove(e);
        commitTransaction();
    }

    @Override
    public void persist(T e) {
        beginTransaction();
        entityManager.persist(e);
        commitTransaction();
    }

    @Override
    public void update(T e, Consumer<T>... updates) {
        beginTransaction();
        Arrays.stream(updates).forEach(up -> up.accept(e));
        commitTransaction();
    }

    private void beginTransaction() {
        try {
            entityManager.getTransaction().begin();
        } catch (IllegalStateException e) {
            rollBackTransaction();
        }
    }

    private void commitTransaction() {
        try {
            entityManager.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            rollBackTransaction();
        }
    }

    private void rollBackTransaction() {
        try {
            entityManager.getTransaction().rollback();
        } catch (IllegalStateException | PersistenceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if(entityManager != null && entityManager.isOpen())
            entityManager.close();
    }
}
