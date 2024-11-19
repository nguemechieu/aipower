package com.sopotek.aipower.routes.auth;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
public class RegistrationController {


    protected UserRepository userRepository;
@Autowired
    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    protected PasswordEncoder passwordEncoder;

    @PostMapping("/api/v3/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        try {
            // Check if the username already exists
            User existingUser = userRepository.findByUsernameAndPassword(newUser.getUsername(),newUser.getPassword()).get();
            if (existingUser.isEnabled()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
            }

            // Check if email already exists
            Optional<User> existingEmail = userRepository.findByEmail(newUser.getEmail());
            if (existingEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            }

            // Set default values
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Hash the password
            newUser.setEnabled(true); // Enable the user by default
            newUser.setRole("USER"); // Default role
            newUser.setAccountCreationDate(LocalDate.now());
            newUser.setLastLoginDate(LocalDate.now());

            // Save the user
            userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user: " + e.getMessage());
        }
    }
}
