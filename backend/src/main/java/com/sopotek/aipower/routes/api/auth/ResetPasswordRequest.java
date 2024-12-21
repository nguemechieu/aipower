package com.sopotek.aipower.routes.api.auth;

import com.sopotek.aipower.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Component
public class ResetPasswordRequest {
    private String newPassword;
    private String currentPassword;

    private UserRepository userRepository;
    @Autowired
    public ResetPasswordRequest(UserRepository userRepository){
        this.userRepository = userRepository;
    };

    public String getResetToken() {
        // Generate a random reset token and save it to the database
        // Update the resetToken field in the User entity
        // Return the resetToken
        return UUID.randomUUID().toString(); // Placeholder for a real implementation




    }

    public Instant getResetTokenExpiryTime() {
        // Calculate and return the expiry time for the reset token
        // The expiry time should be 2 hours after the current time
        // Placeholder for a real implementation
        return Instant.now().plusSeconds(7200); // Placeholder for a real implementation
    }
}
