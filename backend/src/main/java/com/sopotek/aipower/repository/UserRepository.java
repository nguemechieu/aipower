package com.sopotek.aipower.repository;

import com.sopotek.aipower.model.User;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (Spring Data JPA automatically handles this query)
    Optional<User> findByEmail(String email);

    // Find user by username (Spring Data JPA automatically handles this query)
    Optional<User> findByUsername(String username);

    // Update failed login attempts for the user
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedLoginAttempts = :failedLoginAttempts WHERE u.id = :id")
    void updateFailedLoginAttempts(@Param("failedLoginAttempts") int failedLoginAttempts, @Param("id") Long id);

    // Update reset token for the user
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.resetToken = :resetToken WHERE u.id = :id")
    void updateResetToken(@Param("resetToken") String resetToken, @Param("id") Long id);

    // Find user by reset token
    Optional<User> findByResetToken(String token);



}
