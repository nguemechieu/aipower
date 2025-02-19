package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RoleRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public RoleRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @CacheEvict(value = "roles", key = "#role.id")
    public void update(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.merge(role);
        }
    }





  }
