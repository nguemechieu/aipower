package com.sopotek.aipower;


import com.sopotek.aipower.model.User;


import com.sopotek.aipower.service.Db;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v3/users")
public class UsersController {


    Db userService=new Db();

    public UsersController() {

    }
//User logout
    @Operation(summary = "Logout a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Successfully logged out");
    }

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
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = Optional.ofNullable(userService.getUserById(id));
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(200).body(user);


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
        User us = userService.update(id, updatedItem);

        if (us != null) {
            return ResponseEntity.status(200).body("Item updated successfully");
        }
        return ResponseEntity.status(500).body(
                "No user found with id: %d".formatted(id)
        );

    }

    // DELETE: Remove or delete users by ID
    @Operation(summary = "Delete a user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "500", description = "No user found")
    })
    @DeleteMapping("/delete/id:{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        if (userService.existsById(id)) {
            userService.deleteById(id);

            return ResponseEntity.status(200).body("User deleted successfully");
        }
        return ResponseEntity.status(500).body(
                "No user found with id: %d".formatted(id)
        );
    }


}