package ru.duzhinsky.preorderbot.entities.repositories;

import jakarta.persistence.EntityManager;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.dao.impl.TgChatDaoImpl;

public class TgChatRepository {
    private final TgChatDaoImpl tgChatDao;

    public TgChatRepository(EntityManager entityManager) {
        tgChatDao = new TgChatDaoImpl(entityManager);
    }

    public void create(TgChat tgChat) {
        tgChatDao.beginTransaction();
        tgChatDao.create(tgChat);
        tgChatDao.commit();
    }

    public TgChat findById(Long id) {
        return tgChatDao.findById(id);
    }
}
