package com.sopotek.aipower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.host}")
    private String from;

    @Autowired
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // Ensure this property is set
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // You can add more methods here for sending HTML emails, attachments, etc.
}
