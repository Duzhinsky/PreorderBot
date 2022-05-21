package ru.duzhinsky.preorderbot.persistence.dao;

import jakarta.persistence.EntityManagerFactory;
import ru.duzhinsky.preorderbot.persistence.EntityManagerUtil;

import java.io.Serializable;

public class JpaDaoFactory<T, Id extends Serializable> extends DaoFactory<T, Id> {
    private static final EntityManagerFactory emFactory = EntityManagerUtil.getEntityManagerFactory();

    @Override
    public EntityDao<T, Id> getDao(Class<T> clazz) {
        return new JpaEntityDao<T, Id>(emFactory.createEntityManager(), clazz);
    }
}
