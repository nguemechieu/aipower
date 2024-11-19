package com.sopotek.aipower;

import com.hazelcast.config.Config;
import com.hazelcast.config.MetricsJmxConfig;
import com.hazelcast.config.MetricsManagementCenterConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
@EntityScan(basePackages = "com.sopotek.aipower.model")
@EnableAdminServer
@EnableCaching
@EnableJpaRepositories
@EnableGlobalAuthentication
@EnableScheduling
@EnableTransactionManagement

public class AipowerApplication {

    public AipowerApplication() {
    }

    public static void main(String[] args) {
            Dotenv dotenv = Dotenv.load();

            // Set system properties to make variables accessible as environment variables
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

            SpringApplication.run(AipowerApplication.class, args);
        }

    }
