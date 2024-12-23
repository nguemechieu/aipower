package com.sopotek.aipower;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.github.cdimascio.dotenv.Dotenv;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling

@EnableBatchProcessing
@EntityScan(basePackages = "com.sopotek.aipower.domain")

@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
@EnableAdminServer
public class AipowerServer {
    private static final Log LOG = LogFactory.getLog(AipowerServer.class);
    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        LOG.info("Environment variables loaded successfully.");
        // Start Spring Boot Application
        SpringApplication application = new SpringApplication(AipowerServer.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
    }
}


