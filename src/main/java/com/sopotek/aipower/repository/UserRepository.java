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

    // Find user by email
    @Query(
            "SELECT u FROM User u WHERE u.email = :email"
            // If you want to use a custom query, you can specify it here instead of using the @Query annotation.
            // For example:
            // query = "SELECT u FROM User u WHERE u.email = :email AND u.expiryDate > CURRENT_DATE"
            // Note: This query assumes the expiryDate field exists in the User entity. If it doesn't, you may need to adjust the query accordingly.

    )


    Optional<User> findByEmail(String email);

    // Find user by reset token and email

    // If you want to use a custom query, you can specify it here instead of using the @Query annotation.


    Optional<User> findByResetToken(String token);


    // Find user by username
    @Query("SELECT u FROM User u WHERE u.username = :username")
    // If you want to use a custom query, you can specify it here instead of using the @Query annotation.
    Optional<User> findByUsername(@Param("username")String username);

    // Find user by email
    @Query("SELECT u FROM User u WHERE u.email = :email" + " OR u.username = :username")
    // If you want to use a custom query, you can specify it here instead of using the @Query annotation.

    boolean existsByUsernameOrEmail(String username, String email);
}
