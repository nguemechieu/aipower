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
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
@EnableScheduling
@EnableBatchProcessing
@EntityScan(basePackages = "com.sopotek.aipower.domain")
@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
@EnableAdminServer
public class AipowerServer {
    private static final Log LOG = LogFactory.getLog(AipowerServer.class);
    public static void main(String[] args) throws IOException {

        File file = new File(".env");
        //Check if .env file exists if not create one
       if (!file.exists()) {LOG.info("File :"+file.createNewFile()+"  have been created!");}
        // Create a writer for.env file
        try (FileWriter writer = new FileWriter(file)) {
            // Write key-value pairs to.env file
            writer.write("SPRING_APPLICATION_NAME=AIPower\n");
            writer.write("SPRING_APPLICATION_GROUP=AIPower-group\n");
            writer.write("SPRING_APPLICATION_VERSION=1.0.0\n");
            writer.write("\n");
            String secret=gennerateSecretKey();
            writer.write("SPRING_PROFILES_ACTIVE=pro\n");
            writer.write("AIPOWER_SECRET_KEY="+secret+"\n");
            writer.write("AIPOWER_JWT_TOKEN_EXPIRATION_TIME=86400\n");
            writer.write("AIPOWER_JWT_REFRESH_TOKEN_EXPIRATION_TIME=86400000\n");
            writer.write("AIPOWER_JWT_ACCESS_TOKEN_VALIDITY_SEC=60\n");
            writer.write("AIPOWER_JWT_REFRESH_TOKEN_VALIDITY_SEC=86400000\n");
            writer.write("\n");
            writer.write("LOG_CLEANUP_FILE_PATH=/path/to/logs/logfile.log\n");
            writer.write("LOG_CLEANUP_SIZE_THRESHOLD=10485760\n");
            writer.write("\n");
            writer.write("SERVER_ERROR_INCLUDE_BINDING_ERRORS=always\n");
            writer.write("\n");
            writer.write("# --- Logging Configuration ---\n");
            writer.write("LOGGING_LEVEL_ROOT=INFO\n");
            writer.write("LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=INFO\n");
            writer.write("LOGGING_LEVEL_COM_SOPOTEK=DEBUG\n");
            writer.write("\n");
            writer.write("# --- Server Settings ---\n");
            writer.write("SERVER_ADDRESS=localhost\n");
            writer.write("SERVER_PORT=8080\n");
            writer.write("SERVER_SERVLET_CONTEXT_PATH=/\n");
            writer.write("SERVER_SSL_ENABLED=false\n");
            writer.write("SERVER_SSL_KEY_STORE_TYPE=JKS\n");
            writer.write("SERVER_SSL_KEY_STORE=classpath:your_ssl_key_store\n");
            writer.write("SERVER_SSL_KEY_STORE_PASSWORD=your_ssl_key_store_password\n");
            writer.write("SERVER_SSL_KEY_ALIAS=your_ssl_key_alias\n");
            writer.write("\n");
            writer.write("# --- DataSource Configuration ---\n");
            writer.write("SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/aipower\n");
            writer.write("SPRING_DATASOURCE_USERNAME=root\n");
            writer.write("SPRING_DATASOURCE_PASSWORD=root307\n");
            writer.write("HIKARI_MAXIMUM_POOL_SIZE=20\n");
            writer.write("HIKARI_MINIMUM_IDLE=10\n");
            writer.write("HIKARI_IDLE_TIMEOUT=600000\n");
            writer.write("HIKARI_MAX_LIFETIME=1800000\n");
            writer.write("HIKARI_CONNECTION_TIMEOUT=30000\n");
            writer.write("\n");
            writer.write("# --- Admin Server Configuration ---\n");
            writer.write("SPRING_BOOT_ADMIN_CLIENT_URL=http://localhost:8081\n");

            writer.write("# --- TELEGRAM Configuration---\n");
            writer.write("TELEGRAM_BOT_TOKEN=2032573404:AAGnxJpNMJBKqLzvE5q4kGt1cCGF632bP7A\n");
            writer.write("TELEGRAM_CHAT_ID=your-telegram-chat-id\n");
            writer.write("#------ADMIN -- CONFIG --------------------------------\n");
            writer.write("ADMIN_USERNAME=admin\n");
            writer.write("ADMIN_PASSWORD=password\n");
            writer.write("\n");
            writer.write("# --- Mail Configuration ---\n");
            writer.write("MAIL_PROTOCOL=smtp\n");
            writer.write("MAIL_HOST=smtp.gmail.com\n");
            writer.write("MAIL_PORT=587\n");
            writer.write("MAIL_USERNAME=noelmartialnguemechieu@gmail.com\n");
            writer.write("MAIL_PASSWORD=Bigboss307\n");
            writer.write("\n");
            writer.write("# --- Swagger Configuration ---\n");
            writer.write("OPENAI_API_URL=https://api.openai.com/v1/chat/completions\n");
            writer.write("OPENAI_API_KEY=your-openai-api-key\n");
            writer.write("OPENAI_API_MODEL=gpt-3.5-turbo\n");
            writer.write("\n");
            writer.write("# --- Oanda API ---\n");
            writer.write("OANDA_API_URL=https://api-fxtrade.oanda.com/v3");
            writer.write("OANDA_API_ACCOUNT_ID=101-004-1234567-001\n");
            writer.write("OANDA_API_ACCESS_TOKEN=your-oanda-access-token\n");

            writer.write("\n");
            writer.write("# --- Kafka Configuration ---\n");
            writer.write("KAFKA_BOOTSTRAP_SERVERS=127.0.0.1:9092\n");
            writer.write("KAFKA_CLIENT_ID=ai-power-client\n");
            writer.write("KAFKA_CONSUMER_GROUP=ai-power-client-group\n");
            writer.write("KAFKA_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer\n");
            writer.write("KAFKA_TOPIC_NAME=ai-power-topic\n");
            writer.write("KAFKA_GROUP_ID=ai-power-group\n");
            writer.write("KAFKA_ACKS=all\n");
            writer.write("KAFKA_RETRIES=3\n");
            writer.write("KAFKA_BATCH_SIZE=16384\n");
            writer.write("KAFKA_LINGER_MS=1\n");
            writer.write("KAFKA_BUFFER_MEMORY=33554432\n");
            writer.write("KAFKA_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION=5\n");
            writer.write("\n");

            writer.write("# --- Sentry Configuration ---\n");
            writer.write("SENTRY_DSN=https://your-sentry-dsn@o365.ingest.sentry.io/1234567890\n");
            writer.write("\n");
            writer.write("#---- BINANCE_US CONFIG----\n");
            writer.write("BINANCEUS_API_KEY=your-binance-api-key\n");
            writer.write("BINANCEUS_API_SECRET=your-binance-api-secret\n");
            writer.write("BINANCEUS_BASE_URL=https://api.binance.us/api/v3\n");
            writer.write("BINANCEUS_TEST_MODE=true\n");
            //Oanda config
            writer.write("#---OANDA --CONFIG --");
            writer.write("OANDA_API_URL=https://api-fxtrade.oanda.com/v3\n");
            writer.write("OANDA_API_ACCOUNT_ID=101-004-1234567-001\n");
            writer.write("OANDA_API_ACCESS_TOKEN=your-oanda-access-token\n");
            writer.write("\n");
writer.write("# --- STELLAR --- NETWORK ---CONFIG\n");
        writer.write("STELLAR_NETWORK=PUBLIC.NETWORK\n");
        writer.write("STELLAR_ACCOUNT_ID=GCHMECI5FKTHJVWVCB7YNGOIA4S2DND2PZCQTPGL7A6IE6YKA5D7PJH5\n");
        writer.write("STELLAR_ACCOUNT_SECRET=SBICUGFB5FSCJCHSRGLZYQSWMDKSSQA5XCZ5FMNKB6CAAKAQ74R2XXR4\n");
        writer.write("STELLAR_BASE_URL=https://horizon-testnet.stellar.org/\n");
        writer.write("STELLAR_MAX_RETRY_ATTEMPTS=3\n");
        writer.write("STELLAR_RETRY_DELAY_MS=1000\n");
        writer.write("\n");
        writer.write("# --- OPENAI --- CHATGPT --- CONFIG");
        writer.write("OPENAI_API_URL=https://api.openai.com/v1/chat/completions\n");
        writer.write("OPENAI_API_KEY=your-openai-api-key\n");
        writer.write("OPENAI_API_MODEL=text-davinci-003\n");

        // GOOGLE AUTH COINFIG
            writer.write("\n");

            //THIS CONFIG MUST START WITH REACT_APP_ SO REACT ENGINE CAN READ THIS ENVIRONMENT
            writer.write("# --- GOOGLE AUTH --- CONFIG");
            writer.write("REACT_APP_GOOGLE_CLIENT_ID=your-google-auth-client-id\n");
            writer.write("REACT_APP_GOOGLE_CLIENT_SECRET=your-google-auth-client-secret\n");
            writer.write("REACT_APP_GOOGLE_CLIENT_REDIRECT_URI=http://localhost:8080/auth/google/callback\n");
            writer.write("\n");

            //GITHUB AUTH CONFIG
            writer.write("# --- GITHUB AUTH --- CONFIG");
            writer.write("REACT_APP_GITHUB_CLIENT_ID=252116\n");
            writer.write("REACT_APP_GITHUB_CLIENT_SECRET=Iv1.23779dd826d2df1f\n");
            writer.write("REACT_APP_GITHUB_CLIENT_REDIRECT_URI=http://localhost:8080/auth/github/callback\n");
            writer.write(
                    "HZ_JET_ENABLED=true\n"
            );
            writer.write("\n");





            writer.flush();
        }
        LOG.info("======== Warning ==========\nYou need to set your .env variable with your own environment variable");

        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        // Load environment variables from.env file and set them as system properties
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        LOG.info("Environment variables loaded successfully.");
        // Start Spring Boot Application
        SpringApplication application = new SpringApplication(AipowerServer.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
    }

    private static String gennerateSecretKey() {
        String key = "";
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
           LOG.error(
                   "Error occurred while generating secret key: {}"+e.getMessage()
           );
        }
        return key;
    }
}


