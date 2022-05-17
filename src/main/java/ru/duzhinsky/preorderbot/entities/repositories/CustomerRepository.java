package ru.duzhinsky.preorderbot.entities.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.duzhinsky.preorderbot.entities.Customer;

import javax.inject.Inject;

public class CustomerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public CustomerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
