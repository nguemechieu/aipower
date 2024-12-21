package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/users")
public class UsersController {

    public static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;
    private final CacheManager cacheManager;

    @Autowired
    public UsersController(UserService userService, CacheManager cacheManager) {
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void initializeCache() {
        Cache cache = cacheManager.getCache("users");
        if (cache == null) {
            throw new IllegalStateException("Cache 'users' not found");
        }
        cache.put("testKey", "testValue");
        logger.info("Cache 'users' initialized with test data.");
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
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
            User user = userService.getUserById(id);
            return user != null
                    ? ResponseEntity.ok(user)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching user by ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update a user by ID
    @PutMapping("/update/id:{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            if (!userService.getUserRepository().existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No user found with ID: " + id);
            }
            userService.update(updatedUser);
            return ResponseEntity.ok("User with ID " + id + " updated successfully.");
        } catch (Exception e) {
            logger.error("Error updating user with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete a user by ID
    @DeleteMapping("/delete/id:{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.getUserRepository().existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No user found with ID: " + id);
            }
            userService.deleteById(id);
            return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting user with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
