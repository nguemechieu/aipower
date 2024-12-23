package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sopotek.aipower.routes.api.UsersController.logger;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        }
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void updateFailedLoginAttempts(int attempts, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("UPDATE User SET failedLoginAttempts = :attempts WHERE id = :userId", User.class)
                    .setParameter("attempts", attempts)
                    .setParameter("userId", userId)
                    .executeUpdate();
            transaction.commit();
        }
    }
    @Transactional
    @CacheEvict(value = "users", key = "#user.id")
    public void saveOrUpdate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User object cannot be null.");
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                if (user.getId() != null) {
                    // Check if the user already exists in the database
                    User existingUser = session.get(User.class, user.getId());
                    if (existingUser != null) {
                        // Update existing user
                        session.merge(user);
                        logger.info("User updated successfully with ID: {}", user.getId());
                    } else {
                        // Create new user if ID is provided but doesn't exist in DB
                        session.persist(user);
                        logger.info("New user created with ID: {}", user.getId());
                    }
                } else {
                    // No ID provided, create a new user
                    session.persist(user);
                    logger.info("New user created successfully.");
                }

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                logger.error("Error during save or update: {}", e.getMessage());
                throw new RuntimeException("Error saving or updating user", e);
            }
        } catch (Exception e) {
            logger.error("Error managing session: {}", e.getMessage());
            throw new RuntimeException("Error saving or updating user", e);
        }
    }


    @Transactional(readOnly = true)
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public Optional<User> findByResetToken(String resetToken) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE resetToken = :resetToken", User.class)
                   .setParameter("resetToken", resetToken)
                   .uniqueResultOptional();
        }
    }
}
