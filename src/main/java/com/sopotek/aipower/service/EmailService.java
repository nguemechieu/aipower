package com.sopotek.aipower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.sopotek.aipower.routes.NewsController.logger;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Use username for the "from" address
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple email message.
     *
     * @param to      Recipient's email address
     * @param subject Subject of the email
     * @param text    Body of the email
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            validateEmailParameters(to, subject, text);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception ex) {
            logger.error("Failed to send email: {}", ex.getMessage());

        }
    }

    /**
     * Validates email parameters to ensure they are not null or empty.
     *
     * @param to      Recipient's email address
     * @param subject Email subject
     * @param text    Email body
     */
    private void validateEmailParameters(String to, String subject, String text) {
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("Recipient email address cannot be null or empty.");
        }
        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("Email subject cannot be null or empty.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Email body cannot be null or empty.");
        }
    }
}
