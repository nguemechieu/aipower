package com.sopotek.aipower.repository;

import com.sopotek.aipower.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username and password
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    // Check if username exists
    boolean existsByUsername(String username);

    // Find by email
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    // Find by reset token and email
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.resetToken = :token")
    Optional<User> findByResetTokenAndEmail(@Param("email") String email, @Param("token") String token);


    // Find user by reset token
    @Query("SELECT u FROM User u WHERE u.resetToken = :token")
    Optional<User> findByResetToken(@Param("token") String token);

}
