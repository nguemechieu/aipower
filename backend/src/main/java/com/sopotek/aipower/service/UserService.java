package com.sopotek.aipower.service;

import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.RoleRepository;
import com.sopotek.aipower.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
public class UserService {

    private  UserRepository userRepository;
    private RoleRepository roleRepository;
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public UserService() {

    }

    @Transactional
    public void updateFailedLoginAttempts(Long userId, int attempts) {
        userRepository.updateFailedLoginAttempts(attempts, userId);
    }

    @Transactional
    public ResponseEntity<?> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                user.get().resetFailedLoginAttempts();
                return ResponseEntity.ok(user.get());
            } else {
                updateFailedLoginAttempts(user.get().getId(), user.get().getFailedLoginAttempts() + 1);
                if (user.get().getFailedLoginAttempts() >= 5) {
                    return ResponseEntity.status(429).body("Too many failed login attempts. Please try again later.");
                }
                return ResponseEntity.status(401).body("Invalid username or password.");
            }
        }
        return ResponseEntity.status(401).body("Invalid username or password.");
    }




    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void update(User user) {
        userRepository.saveOrUpdate(user);
    }



    public void saveOrUpdate(User user) {
        userRepository.saveOrUpdate(user);
    }
}
