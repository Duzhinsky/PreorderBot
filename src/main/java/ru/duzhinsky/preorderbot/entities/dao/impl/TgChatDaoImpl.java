package ru.duzhinsky.preorderbot.entities.dao.impl;

import jakarta.persistence.EntityManager;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.dao.TgChatDao;


public class TgChatDaoImpl implements TgChatDao {
    private final EntityManager currentEntityManager;

    public TgChatDaoImpl(EntityManager currentEntityManager) {
        this.currentEntityManager = currentEntityManager;
    }

    public void beginTransaction() {
        currentEntityManager.getTransaction().begin();
    }

    public void commit() {
        currentEntityManager.getTransaction().commit();
    }

    @Override
    public void create(TgChat entity) {
        currentEntityManager.persist(entity);
    }

    @Override
    public void update(TgChat entity) {
        currentEntityManager.merge(entity);
    }

    @Override
    public TgChat findById(Long id) {
        return currentEntityManager.find(TgChat.class, id);
    }

    @Override
    public void delete(TgChat entity) {
        currentEntityManager.remove(entity);
    }
}
