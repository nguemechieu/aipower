package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
public class UserService {
    private static final Log LOG = LogFactory.getLog(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    @Cacheable(value = "users", key = "#username")
    public boolean authenticate(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.isPresent() &&
                passwordEncoder.matches(password, optionalUser.get().getPassword());
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
    @CacheEvict(value = "users", key = "#updatedItem.id")
    public void update(User updatedItem) {
        userRepository.save(updatedItem);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        LOG.info("Deleted user with ID: {}");
    }

    public Optional<User> findByUsername(@NotBlank(message = "Username is required") String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsername(username).isPresent() ||
                userRepository.findByEmail(email).isPresent();
    }

    public void saveUser(@Valid User user) {
        if (existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new IllegalArgumentException("User with the same username or email already exists");
        }
        userRepository.save(user);
    }

    @Transactional
    public List<GrantedAuthority> getAuthoritiesByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();
        return user.getRoles()
                .stream()
                .map(role -> (GrantedAuthority) role)
                .toList();
    }
}
