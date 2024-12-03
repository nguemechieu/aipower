package com.sopotek.aipower.routes;


import com.sopotek.aipower.model.User;

import com.sopotek.aipower.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sopotek.aipower.routes.NewsController.logger;


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


    @Operation(summary = "Get all users from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all users"),
            @ApiResponse(responseCode = "404", description = "No users found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")


    })
    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        return ResponseEntity.ok(users);
    }

    @Operation(summary = " Get a single user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a user"),
            @ApiResponse(responseCode = "404", description = "No user found")
    })
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

//Get Me
    @Operation(summary = "Get the current logged-in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the current user"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(user);
    }
    // PUT: Update an existing users by ID
    @Operation(summary = "Update a user in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "500", description = "No user found")
    })
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
    @Operation(summary = "Delete a user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "500", description = "No user found")
    })
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