package ru.duzhinsky.preorderbot.persistence.dao;

import jakarta.persistence.EntityManagerFactory;
import ru.duzhinsky.preorderbot.persistence.EntityManagerUtil;

import java.io.Serializable;

public class JPADAOFactory<T, Id extends Serializable> extends DaoFactory<T, Id> {
    private static final EntityManagerFactory emFactory = EntityManagerUtil.getEntityManagerFactory();

    @Override
    public EntityDAO<T, Id> getDao(Class<T> clazz) {
        return new JPAEntityDao<T, Id>(emFactory.createEntityManager(), clazz);
    }
}
