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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;


        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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






    public User getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User?
                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
    }
}
