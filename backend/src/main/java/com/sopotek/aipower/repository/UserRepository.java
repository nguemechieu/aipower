package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.Role;
import com.sopotek.aipower.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private final SessionFactory sessionFactory;
    private Transaction transaction;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            logger.error("Error finding User by ID: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding User by ID", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding User by username: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding User by username", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding User by email: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding User by email", e);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void updateFailedLoginAttempts(int attempts, Long userId) {
        Session session = sessionFactory.openSession();
        try (session) {
            transaction = session.beginTransaction();
            session.createQuery("UPDATE User SET failedLoginAttempts = :attempts WHERE id = :userId")
                    .setParameter("attempts", attempts)
                    .setParameter("userId", userId)
                    .executeUpdate();
            transaction.commit();
            logger.info("Failed login attempts updated for userId: {}", userId);
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error updating failed login attempts: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating failed login attempts", e);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void saveOrUpdate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User object cannot be null.");
        }

        Session session = sessionFactory.openSession();
        try (session) {
             transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("User saved or updated successfully: {}", user.getId());
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error saving or updating User: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving or updating User", e);
        }
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving all Users: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all Users", e);
        }
    }

    public Optional<User> findByResetToken(String resetToken) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE resetToken = :resetToken", User.class)
                    .setParameter("resetToken", resetToken)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding User by reset token: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding User by reset token", e);
        }
    }

    public void create(User user) {
        Session session = sessionFactory.openSession();
        try (session) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User created successfully with ID: {}", user.getId());
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error creating User: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating User", e);
        }
    }

    public void save(Role role) {
        Session session = sessionFactory.openSession();
        try (session) {
            transaction = session.beginTransaction();
            session.persist(role);
            transaction.commit();
            logger.info("Role saved successfully: {}", role);
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Error saving Role: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving Role", e);
        }
    }
}
