package ru.duzhinsky.preorderbot.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class EntityManagerProducer {
    private static final EntityManagerFactory entityManagerFactory;

    static
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("Hibernate");
    }

    @Produces
    public EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("Context is not initialized yet.");
        }
        return entityManagerFactory.createEntityManager();
    }

    public void close(@Disposes EntityManager entityManager) {
        if(entityManager.isOpen())
            entityManager.close();
    }
}
