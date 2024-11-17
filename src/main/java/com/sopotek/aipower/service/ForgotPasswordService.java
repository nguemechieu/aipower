package com.sopotek.aipower.service;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    @Value("${spring.mail.host}")
    private String smtp;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String username;

    private final UserRepository userRepository;

    @Autowired
    public ForgotPasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private @NotNull Session getMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtp);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.username", username);
        props.put("mail.smtp.password", password);


        return Session.getInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Email not found");
        }

        User user = userOptional.get();
        String resetToken = generateResetToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiryTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        sendResetEmail(user.getEmail(), resetToken);
    }

    private void sendResetEmail(String recipientEmail, String resetToken) {
        String resetUrl = appUrl + "/reset-password?token=" + resetToken;
        String subject = "Password Reset Request";
        String message = "Dear User,\n\n" +
                "We received a request to reset your password. You can reset your password by clicking the link below:\n\n" +
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email. The link will expire in 1 hour.\n\n" +
                "Best regards,\n" +
                "The AiPower Team";

        try {
            Session session = getMailSession();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public boolean isResetTokenValid(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        return user.getResetTokenExpiryTime() != null && user.getResetTokenExpiryTime().isAfter(LocalDateTime.now());
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        User user = userOptional.get();
        user.setPassword(newPassword); // Ensure you hash the password before saving
        user.setResetToken(null);
        user.setResetTokenExpiryTime(null);
        userRepository.save(user);
    }
}
