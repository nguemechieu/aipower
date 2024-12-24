package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RoleRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public RoleRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#id")
    public Role findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Role.class, id);
        }
    }

    @Transactional
    @CacheEvict(value = "roles", key = "#id")
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Role role = session.get(Role.class, id);
            if (role != null) {
                session.remove(role);
            }
        }
    }

    @Transactional
    @CacheEvict(value = "roles", key = "#role.id")
    public void update(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.merge(role);
        }
    }

    @Transactional
    @CacheEvict(value = "roles", key = "#role.id")
    public void saveOrUpdate(@NotNull Role role) {
        if (role.getId() == null) {
            save(role);
        } else {
            update(role);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#name")
    public Role findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Role WHERE name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "roles")
    public List<Role> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Role", Role.class).list();
        }
    }

    @Transactional
    @CacheEvict(value = "roles", key = "#role.id")
    public void save(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.persist(role);
        }
    }
}
