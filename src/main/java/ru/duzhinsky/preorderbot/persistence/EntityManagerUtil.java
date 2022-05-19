package ru.duzhinsky.preorderbot.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {
    private static EntityManagerFactory emf = initEntityManagerFactory();

    public static EntityManagerFactory initEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory("Hibernate");
        } catch (Throwable t) {
            //todo add logger
            throw new ExceptionInInitializerError(t);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if(emf == null)
            emf = initEntityManagerFactory();
        return emf;
    }

    public static void close() {
        emf.close();
    }

}
