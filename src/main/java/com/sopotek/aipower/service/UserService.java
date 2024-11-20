package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@Setter
@Service
public class UserService {
private static final Log LOG = LogFactory.getLog(UserService.class);
    private final EmailService emailService;
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;


    }

    /**
     * Authenticates a user based on username and password.
     *
     * @param username the username
     * @param password the raw password
     * @return true if the user exists and the password matches; false otherwise
     */
    @Transactional

@Cacheable(
        value = "users",
        key = "#username"

)
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password).get();
        return passwordEncoder.matches(password, user.getPassword());
    }




    @Transactional
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        LOG.info("Fetching user by ID from the database.");
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        LOG.info("Fetching all users from the database.");
        return userRepository.findAll();
    }
    @Transactional
    public User update(Long id, User updatedItem) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(updatedItem.getUsername());
            user.setPassword(passwordEncoder.encode(updatedItem.getPassword()));
            userRepository.save(user);
            return user;
        }
        return null;
    }

    @Transactional
    @Cacheable(value = "users", key = "#id")
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        LOG.info("Deleted user with ID: " + id);
    }




    @Transactional
    @Cacheable(value = "users", key = "#id")
    public String updateUser(Long id, String uname) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(uname);
            userRepository.save(user);
            return "Username updated successfully";
        }
        return "No user found with id: " + id;
    }
}
