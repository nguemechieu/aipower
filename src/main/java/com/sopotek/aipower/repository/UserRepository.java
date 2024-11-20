package com.sopotek.aipower.repository;

import com.sopotek.aipower.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username and password
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    // Check if a user with the given username exists
    boolean existsByUsername(String username);

    // Check if a user with the given email exists
    boolean existsByEmail(String email);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by reset token and email
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.resetToken = :token")
    Optional<User> findByResetTokenAndEmail(@Param("email") String email, @Param("token") String token);

    // Find user by reset token
    Optional<User> findByResetToken(String token);

    // Save a user (no need for a custom query; JpaRepository provides a built-in save method)
    // User save(User user); // This is already provided by JpaRepository
}
