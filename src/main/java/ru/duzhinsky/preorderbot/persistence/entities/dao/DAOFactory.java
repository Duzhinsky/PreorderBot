package ru.duzhinsky.preorderbot.persistence.entities.dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DAOFactory {
    private static final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("Hibernate");

    public static TgChatDAO getTgChatDAO() {
        return new TgChatDAO(emFactory.createEntityManager());
    }
}
