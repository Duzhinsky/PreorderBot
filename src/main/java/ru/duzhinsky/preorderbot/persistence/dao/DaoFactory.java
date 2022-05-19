package ru.duzhinsky.preorderbot.persistence.dao;

import java.io.Serializable;

public abstract class DaoFactory<T, Id extends Serializable> {
    public abstract EntityDAO<T, Id> getDao(Class<T> clazz);
}
