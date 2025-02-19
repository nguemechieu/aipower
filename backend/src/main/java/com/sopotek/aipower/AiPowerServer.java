package com.sopotek.aipower;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

@EnableScheduling
@EnableBatchProcessing
@EntityScan(basePackages = "com.sopotek.aipower.domain")
@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
@EnableAdminServer
public class AiPowerServer {

    private static final Logger LOG = LoggerFactory.getLogger(AiPowerServer.class);
    private static final String ENV_FILE_PATH = ".env";


    public static void main(String[] args) {
        try {


            // Start the Spring Boot application
            SpringApplication application = new SpringApplication(AiPowerServer.class);
            application.setBannerMode(Banner.Mode.CONSOLE);
            application.setLogStartupInfo(true);
            // Load environment variables from .env file
            loadEnvironmentVariables();


            application.run(args);

        } catch (IOException e) {
            LOG.error("Error during application initialization: {}", e.getMessage());
        }
    }



    /**
     * Loads environment variables and writes them to the .env file if it doesn't exist.
     */
    private static void loadEnvironmentVariables() throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + ENV_FILE_PATH);

        if (!file.exists()) {
            LOG.info("Creating .env file...");
            if (file.createNewFile()) {
                LOG.info("File created successfully: {}", file.getAbsolutePath());
            }
        }

        // Write environment variables to the .env file
        try (FileWriter writer = new FileWriter(file, true)) {
            writeEnvironmentVariables(writer);
        } catch (IOException e) {
            LOG.error("Error writing to .env file: {}", e.getMessage());
        }
    }

    /**
     * Write environment variable key-value pairs into the .env file.
     */
    private static void writeEnvironmentVariables(FileWriter writer) throws IOException {
        writer.write("SPRING_APPLICATION_NAME=AIPOWER\n");
        writer.write("SPRING_APPLICATION_GROUP=AIPOWER-GROUP\n");
        writer.write("SPRING_APPLICATION_VERSION=1.0.0\n");
        writer.write("AIPOWER_SECRET_KEY=" + generateSecretKey() + "\n");
        writer.write("SPRING_PROFILES_ACTIVE=PRO\n");

        // Other environment variables...
        writer.write("MAIL_PROTOCOL=SMTP\n");
        writer.write("MAIL_HOST=SMTP.GMAIL.COM\n");
        writer.write("MAIL_PORT=587\n");
        writer.write("MAIL_USERNAME=NOELMARTIALNGUEMECHIEU@GMAIL.COM\n");
        writer.write("MAIL_PASSWORD=BIGBOSS307$\n");
        writer.write("STELLAR_NETWORK_SECRET_KEY=SDYAPMSEK2N4LYRFROWHE4SK4LFXF2T2OMCU3BVDAJTEAYKHT4ESKOJ6\n");

        // Additional configurations...
        writer.write("# --- LOGGING CONFIGURATION ---\n");
        writer.write("LOGGING_LEVEL_ROOT=INFO\n");
        writer.write("LOGGING_LEVEL_COM_SOPOTEK=DEBUG\n");
        writer.write("DOCKER_COMPOSE=compose.yaml\n");

        // Database Configuration
        writer.write("# --- DATABASE CONFIGURATION ---\n");
        writer.write("SPRING_DATASOURCE_URL=JDBC:MSQL://LOCALHOST:3306/AIPOWER\n");
        writer.write("SPRING_DATASOURCE_USERNAME=ROOT\n");
        writer.write("SPRING_DATASOURCE_PASSWORD=ADMIN123\n");
        writer.write("SPRING_DATASOURCE_DRIVER_CLASS_NAME=COM.MICROSOFT.SQLSERVER.JDBC.SQLSERVERDRIVER\n");

        // Security Configuration
        writer.write("# --- SECURITY CONFIGURATION ---\n");
        writer.write("SPRING_SECURITY_USER_NAME=ADMIN\n");
        writer.write("SPRING_SECURITY_USER_PASSWORD=ADMIN123\n");
        writer.write("SPRING_SECURITY_USER_ROLES=ADMIN\n");

        // Admin Server Configuration
        writer.write("# --- ADMIN SERVER CONFIGURATION ---\n");
        writer.write("SPRING_BOOT_ADMIN_SERVER_URL=HTTP://LOCALHOST:8080\n");
        writer.write("SPRING_BOOT_ADMIN_SERVER_CONTEXT_PATH=/ADMIN\n");

        // Batch Configuration
        writer.write("# --- BATCH CONFIGURATION ---\n");
        writer.write("SPRING_BATCH_JOB_ENABLED=TRUE\n");
        writer.write("SPRING_BATCH_JOB_NAMES=IMPORT-DATA-JOB\n");
        writer.write("SPRING_BATCH_JOB_FLOW_NAMES=IMPORT-DATA-FLOW\n");
        writer.write("SPRING_BATCH_JOB_DEFAULT_INCREMENTER_NAME=JOB-INCREMENTER\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_READER_ITEM_READER_NAME=CSV-READER\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_NAME=CSV-WRITER\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_CHUNK_SIZE=100\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_READER_FILE_NAME=EMPLOYEES.CSV\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_READER_DELIMITED_TEXT_INPUT_READER_DELIMITER=,\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_ITEM_TYPE=COM.SOPOTEK.AIPOWER.DOMAIN.MODEL.EMPLOYEE\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_DATABASE_SCHEMA_UPDATE=TRUE\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_TABLE_NAME=EMPLOYEES\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_USE_IDENTITY_COLUMN=TRUE\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_IDENTITY_COLUMN_NAME=ID\n");
        writer.write("SPRING_BATCH_JOB_FLOW_IMPORT_DATA_FLOW_STEP_READ_DATA_WRITER_ITEM_WRITER_QUERY=INSERT INTO EMPLOYEES (NAME, EMAIL, DEPARTMENT) VALUES (?,?,?)\n");

        writer.flush();
        LOG.info("Environment variables written to .env file.");
    }


    /**
     * Generates a secret key for the application.
     */
    private static @Nullable String generateSecretKey() {

        // Create admin user on application startup

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error occurred while generating secret key: {}", e.getMessage());
            return null;
        }
    }
}
