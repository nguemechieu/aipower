package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.PersistentLogin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PersistentLoginDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public PersistentLoginDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Save or update a PersistentLogin entity
    public void save(PersistentLogin persistentLogin) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(persistentLogin);
        transaction.commit();
        session.close();
    }

    // Find a PersistentLogin by series
    public Optional<PersistentLogin> findBySeries(String series) {
        Session session = sessionFactory.openSession();
        PersistentLogin login = session.get(PersistentLogin.class, series);
        session.close();
        return Optional.ofNullable(login);
    }

    // Delete all tokens for a username
    public void deleteByUsername(String username) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.createNamedStoredProcedureQuery("DELETE FROM PersistentLogin WHERE username = :username")
                .setParameter("username", username)
                .executeUpdate();
        transaction.commit();
        session.close();
    }
}
