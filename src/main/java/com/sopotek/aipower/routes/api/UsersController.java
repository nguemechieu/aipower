package com.sopotek.aipower.routes.api;


import com.sopotek.aipower.model.User;

import com.sopotek.aipower.service.UserService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sopotek.aipower.routes.api.NewsController.logger;


@RestController
@RequestMapping("/users")
public class UsersController {

    private final CacheManager cacheManager;
@Autowired
    public UsersController(UserService userService, CacheManager cacheManager) {
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void testCache() {
        Cache cache = cacheManager.getCache("users");
        if (cache == null) {
            throw new IllegalStateException("Cache 'users' not found");
        }
        cache.put("testKey", "testValue");
    }

    private final UserService userService;



    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        return ResponseEntity.ok(users);
    }


    // Get a single user by ID
    @GetMapping("/id:{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

         try {
             User user = userService.getUserById(id);
             if (user == null) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
             }
             return ResponseEntity.ok(user);
         }
         catch(Exception e){
             logger.error(
                     e.getMessage(), e);
             return ResponseEntity.status(500).body(null);
         }


    }


    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(user);
    }

// Update a user by ID (PUT method)
    @PutMapping("/update/id:{id}")
    public ResponseEntity<String> updateItem(@PathVariable Long id, @RequestBody User updatedItem) {

  try{
         userService.update(updatedItem);return ResponseEntity.status(200).body(id+" updated successfully");
   }
  catch(Exception e){
           return ResponseEntity.status(500).body(e.getMessage());
        }


    }

    // DELETE: Remove or delete users by ID

    @DeleteMapping("/delete/id:{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        if (userService.getUserRepository().existsById(id)) {
            userService.deleteById(id);

            return ResponseEntity.status(200).body("User deleted successfully");
        }
        return ResponseEntity.status(500).body(
                "No user found with id: %d".formatted(id)
        );
    }


}