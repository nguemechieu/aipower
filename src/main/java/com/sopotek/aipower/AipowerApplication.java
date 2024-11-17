package com.sopotek.aipower;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
@EntityScan(basePackages = "com.sopotek.aipower.model")
@EnableAdminServer


public class AipowerApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        // Set system properties to make variables accessible as environment variables
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(AipowerApplication.class, args);
    }

}
