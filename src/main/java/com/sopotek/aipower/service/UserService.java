package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Service
public class UserService {


    private UserRepository userRepository;
@Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    private PasswordEncoder passwordEncoder;

    @Value("${application.secret.key}")
    private String jwtSecret;

    private static final int JWT_EXPIRATION_MS = 3600 * 1000; // 1 hour in milliseconds

    /**
     * Authenticates a user based on username and password.
     *
     * @param username the username
     * @param password the raw password
     * @return true if the user exists and the password matches; false otherwise
     */
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username,password);
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

    public boolean isUserPasswordValid(String username, String password) {
        User user = userRepository.findByUsername(username,password);
        return user!= null && passwordEncoder.matches(password, user.getPassword());
    }

    public void saveUser(@NotNull User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User update(Long id, User updatedItem) {
        User user = userRepository.findById(id).orElse(null);
        if (user!= null) {
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

    public void deleteById(Long id) {
            userRepository.deleteById(id);
    }
}
