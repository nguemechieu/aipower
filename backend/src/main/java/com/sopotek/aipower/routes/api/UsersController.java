package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.domain.User;

import com.sopotek.aipower.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@RestController
@RequestMapping("/api/v3/users")
public class UsersController {

    public static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private  UserRepository userService;
    private  CacheManager cacheManager;

    @Autowired
    public UsersController(UserRepository userService, CacheManager cacheManager) {
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void initializeCache() {
        Collection<String> cache = cacheManager.getCacheNames();

            logger.info("Created cache: {}", cache);


    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(users);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a user by ID
    @GetMapping("/id:{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = Optional.ofNullable(userService.findById(id));
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));


        } catch (Exception e) {
            logger.error("Error fetching user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update a user by ID
    @PutMapping("/update/id:{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            Optional<User> user = Optional.ofNullable(userService.findById(id));
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                       .body("No user found with ID: " + id);
            }
            updatedUser.setId(id);
            userService.saveOrUpdate(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("User with ID " + id + " updated successfully.");

    }
}
