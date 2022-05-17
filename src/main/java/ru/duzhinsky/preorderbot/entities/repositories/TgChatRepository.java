package ru.duzhinsky.preorderbot.entities.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ru.duzhinsky.preorderbot.entities.TgChat;

import javax.inject.Inject;

public class TgChatRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public TgChatRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void persist(TgChat tgChat) {
        entityManager.getTransaction().begin();
        entityManager.persist(tgChat);
        entityManager.getTransaction().commit();
    }

    public TgChat findById(Long id) {
        return entityManager.find(TgChat.class, id);
    }
}
