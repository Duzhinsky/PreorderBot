package ru.duzhinsky.preorderbot.persistence.entities.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;

import java.util.Arrays;
import java.util.function.Consumer;

public class TgChatDAO {
    private EntityManager entityManager;

    public TgChatDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TgChat findById(Long id) {
        return entityManager.find(TgChat.class, id);
    }

    public void update(Long id, Consumer<TgChat>... updates) {
        TgChat entity = findById(id);
        Arrays.stream(updates).forEach(up -> up.accept(entity));
        beginTransaction();
        commitTransaction();
    }

    public void save(TgChat entity) {
        beginTransaction();
        entityManager.persist(entity);
        commitTransaction();
    }

    public void remove(Long id) {
        TgChat entity = findById(id);
        beginTransaction();
        entityManager.remove(entity);
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

    public void close() {
        if(entityManager != null && entityManager.isOpen())
            entityManager.close();
    }
}
