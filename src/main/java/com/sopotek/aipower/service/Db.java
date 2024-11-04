package com.sopotek.aipower.service;

import com.sopotek.aipower.model.Trade;
import com.sopotek.aipower.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service
public class Db {

    private static final Logger LOG = Logger.getLogger(Db.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    public Db() {
        // Initialization if necessary

    }

    // Create first admin user if not exists
    @Transactional
    public void createFirstAdminUser() {
        try {
            if (!existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("nuemechieu@live.com");
                adminUser.setPassword("admin307$");  // Use BCrypt encryption in production
                adminUser.setCreatedAt(LocalDateTime.now());
                adminUser.setUpdatedAt(LocalDateTime.now());
                adminUser.setFirstName("NOEL");
                adminUser.setMiddleName("MARTIAL");
                adminUser.setLastName("NGUEMECHIEU");
                adminUser.setBirthdate(LocalDate.of(1990, 4, 3));
                adminUser.setPhoneNumber("3023176610");
                adminUser.setRoles(Collections.singletonList("ADMIN"));
                adminUser.setGender("MALE");
                adminUser.setProfilePictureUrl("https://example.com/admin_profile_picture.jpg");
                adminUser.setBio("I'm an AI powered by Sopotek AI Power. I'm here to help you with your finance needs.");
                adminUser.setSecurityQuestion("What's your favorite color?");
                adminUser.setSecurityAnswer("Blue");

                entityManager.persist(adminUser);
            }
        } catch (Exception e) {
            LOG.severe("Error creating first admin user: " + e.getMessage());
        }
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        try {
            return entityManager.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            LOG.severe("Error fetching all users: " + e.getMessage());
            return null;
        }
    }

    // Find user by ID
    public User getUserById(Long id) {
        try {
            return entityManager.find(User.class, id);
        } catch (Exception e) {
            LOG.severe("Error fetching user with ID: " + id + ", error: " + e.getMessage());
            return null;
        }
    }

    // Insert a new user
    @Transactional
    public boolean saveUser(User user) {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            LOG.severe("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // Update a user by ID
    @Transactional
    public void updateUser(Long id, User userDetails) {
        try {
            User existingUser = entityManager.find(User.class, id);
            if (existingUser != null) {
                existingUser.setUsername(userDetails.getUsername());
                existingUser.setEmail(userDetails.getEmail());
                existingUser.setPassword(userDetails.getPassword());
                // Additional fields can be updated here as needed
            }
        } catch (Exception e) {
            LOG.severe("Error updating user with ID: " + id + ", error: " + e.getMessage());
        }
    }

    // Delete user by ID
    @Transactional
    public void deleteUser(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
            }
        } catch (Exception e) {
            LOG.severe("Error deleting user with ID: " + id + ", error: " + e.getMessage());
        }
    }

    // Check if a user exists by username
    public boolean existsByUsername(String username) {
        try {
            return !entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultList()
                    .isEmpty();
        } catch (Exception e) {
            LOG.severe("Error checking existence of user with username: " + username + ", error: " + e.getMessage());
            return false;
        }
    }

    // Authenticate user
    public boolean authenticate(User user) {
        try {
            User existingUser = entityManager.createQuery("FROM User WHERE username = :username AND password = :password", User.class)
                    .setParameter("username", user.getUsername())
                    .setParameter("password", user.getPassword())
                    .getSingleResult();
            return existingUser != null;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            LOG.severe("Error during authentication, error: " + e.getMessage());
            return false;
        }
    }

    // Query all trades
    public List<Trade> queryTrades() {
        try {
            return entityManager.createQuery("FROM Trade", Trade.class).getResultList();
        } catch (Exception e) {
            LOG.severe("Error querying trades: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Insert new trade
    @Transactional
    public void saveTrade(Trade newTrade) {
        try {
            entityManager.persist(newTrade);
        } catch (Exception e) {
            LOG.severe("Error saving trade: " + e.getMessage());
        }
    }

    public User getUserByEmail(String email) {
        try {
            return entityManager.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            LOG.severe("Error finding user by email: " + e.getMessage());
            return null;
        }
    }

    public void savePasswordResetToken(Long id, String resetToken, LocalDateTime expiryTime) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                user.setResetToken(resetToken);
                user.setResetTokenExpiryTime(expiryTime);
            }
        } catch (Exception e) {
            LOG.severe("Error saving password reset token: " + e.getMessage());
        }
    }

    public List<Trade> getAllTrades() {
        try {
            return entityManager.createQuery("FROM Trade", Trade.class).getResultList();
        } catch (Exception e) {
            LOG.severe("Error querying all trades: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public User getUserByUsername(String username) {
        try {
            return entityManager.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            LOG.severe("Error finding user by username: " + e.getMessage());
            return null;
        }
    }

    public void registerUser(User user) {
        try {
            entityManager.persist(user);
        } catch (Exception e) {
            LOG.severe("Error registering user: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            User existingUser = entityManager.createQuery("FROM User WHERE username = :username AND password = :password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            return existingUser!= null;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            LOG.severe("Error during authentication, error: " + e.getMessage());
            return false;
        }
    }

    public User update(Long id, User updatedItem) {
        try {
            User user = entityManager.find(User.class, id);
            if (user!= null) {
                user.setFirstName(updatedItem.getFirstName());
                user.setMiddleName(updatedItem.getMiddleName());
                user.setLastName(updatedItem.getLastName());
                user.setBirthdate(updatedItem.getBirthdate());
                user.setPhoneNumber(updatedItem.getPhoneNumber());
                user.setRoles(updatedItem.getRoles());
                user.setGender(updatedItem.getGender());
                user.setProfilePictureUrl(updatedItem.getProfilePictureUrl());
                user.setBio(updatedItem.getBio());
                user.setSecurityQuestion(updatedItem.getSecurityQuestion());
                user.setSecurityAnswer(updatedItem.getSecurityAnswer());
                return user;
            }
            return null;
        } catch (Exception e) {
            LOG.severe("Error updating user: " + e);


        }
        return null;
    }

    public boolean existsById(Long id) {
        try {
            return entityManager.find(User.class, id)!= null;
        } catch (Exception e) {
            LOG.severe("Error checking existence of user by ID: " + id);
            return false;
        }
    }

    public void deleteById(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            if (user!= null) {
                entityManager.remove(user);
            }
        } catch (Exception e) {
            LOG.severe("Error deleting user by ID: " + id);
        }



    }
}