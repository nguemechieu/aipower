package com.sopotek.aipower;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.DispatcherServlet;


@EntityScan(basePackages = "com.sopotek.aipower.model")
@EnableAdminServer
@EnableCaching
@EnableJpaRepositories
@EnableGlobalAuthentication
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
public class AipowerApplication {

    private  static final Log LOG = LogFactory.getLog(AipowerApplication.class);
    public AipowerApplication() {

LOG.info("Starting Aipower");
    }


    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        // Set system properties to make variables accessible as environment variables
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));


        SpringApplication.run(AipowerApplication.class, args);
    }

}
