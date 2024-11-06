package com.sopotek.aipower.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public ConfigurableMimeFileTypeMap configurableMimeFileTypeMap() {
        ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

        // Define multiple MIME types in a single string
        String mappings = """
            application/pdf pdf
            image/png png
            image/jpeg jpeg jpg
            text/plain txt
            text/html html htm
            application/json json
            application/xml xml
            """;

        fileTypeMap.setMappings(mappings);

        return fileTypeMap;
    }

    @Bean
    public JavaMailSender getJavaMailSender() throws IOException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();


        // Configure additional mail properties
        Properties props = new Properties();
        props.load(
                MailConfig.class.getClassLoader().getResourceAsStream("./application.properties") // Replace with your application properties file path
        );

        mailSender.setJavaMailProperties(props);
        // Set the mail server host and port
        mailSender.setHost(
                props.getProperty(
                        "spring.mail.host"
                )
        );  // Replace with your SMTP host
        mailSender.setPort(587); // Typical port for TLS; change if needed

        // Set the username and password for authentication
        mailSender.setUsername(
                props.getProperty(
                        "spring.mail.username"
                )  // Replace it with your email username
        ); // Replace it with your email
        mailSender.setPassword(
                props.getProperty(
                        "spring.mail.password"
                )  // Replace it with your email password
        );    // Replace it with your email password
        ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
        mailSender.setDefaultFileTypeMap(fileTypeMap);

        return mailSender;
    }
}
