package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Service
public class UserService {
private static final Log LOG = LogFactory.getLog(UserService.class);
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Value("${application.secret.key}")
    private String jwtSecret;

    private static final int JWT_EXPIRATION_MS = 3600 * 1000; // 1 hour in milliseconds

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on username and password.
     *
     * @param username the username
     * @param password the raw password
     * @return true if the user exists and the password matches; false otherwise
     */
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password).get();
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username
     * @return a JWT token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        // Generate a secure key from the secret
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512) // Use the secure key
                .compact();
    }

    public boolean isUserExist(String username) {
        return userRepository.existsByUsername(username);
    }



    public void saveUser(@NotNull User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
    }

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        LOG.info("Fetching user by ID from the database.");
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        LOG.info("Fetching all users from the database.");
        return userRepository.findAll();
    }

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

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        LOG.info("Deleted user with ID: " + id);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void clearAllUserCache() {
        LOG.info("All entries in 'users' cache cleared.");
    }

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
