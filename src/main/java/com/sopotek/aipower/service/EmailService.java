package com.sopotek.aipower.service;

import com.sopotek.aipower.config.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {

    private final Logger LOG = Logger.getLogger(EmailService.class.getName());

    JavaMailSender mailSender;
    public EmailService() {


    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("AiPower ,Inc"); // Replace with your email or use application property

        try {
            mailSender.send(message);
            LOG.info("Email sent successfully to " + to);
        } catch (Exception e) {
            LOG.info("Error sending email: " + e.getMessage());
            // Handle the exception here, for example, by logging it or rethrowing it
        }
    }
}