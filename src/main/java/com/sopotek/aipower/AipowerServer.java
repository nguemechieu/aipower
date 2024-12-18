package com.sopotek.aipower;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

@EntityScan(basePackages = "com.sopotek.aipower.model")
@EnableAdminServer
@EnableCaching
@EnableJpaRepositories(basePackages = "com.sopotek.aipower.repository")
@EnableGlobalAuthentication
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.sopotek.aipower")
public class AipowerServer {

    private static final Log LOG = LogFactory.getLog(AipowerServer.class);

    private static final int KEY_SIZE_BITS = 256; // Size in bits
    private static final int KEY_SIZE_BYTES = KEY_SIZE_BITS / 8; // Size in bytes

    /**
     * Generates a 256-bit secret key and encodes it as a Base64 string.
     *
     * @return A 256-bit secret key as a Base64 encoded string.
     */
    public static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[KEY_SIZE_BYTES];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public static void main(String[] args) {
        // Load and set environment variables using Dotenv
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        LOG.info("Environment variables loaded successfully.");
        // Start Spring Boot Application
        SpringApplication.run(AipowerServer.class, args);
        // Start Netty Client
        startNettyClient(); // High-performance communication
    }

    /**
     * Writes the secret key to the .env file.
     *
     * @param secretKey The generated secret key.
     */
    private static void writeSecretKeyToEnvFile(String secretKey) {
        try {
            String envPath = ".env";
            String content = "AIPOWER_SECRET_KEY=" + secretKey + System.lineSeparator();
            Files.write(Paths.get(envPath), content.getBytes());
            LOG.info("Secret key written to .env file successfully.");
        } catch (IOException e) {
            LOG.error("Failed to write secret key to .env file: " + e.getMessage(), e);
        }
    }

    /**
     * Starts a simple Netty client that connects to localhost on port 8080.
     */
    private static void startNettyClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(
                            new SimpleClientHandler()
                    );
            // Generate a secret key and write it to the.env file
            String secretKey = generateSecretKey();
           // writeSecretKeyToEnvFile(secretKey);
            LOG.info("Generated and written secret key to.env file.");

            // Connect to the Netty server
            // Replace "localhost" and "8080" with your desired server address and port
            // Make sure the Netty server is running before running the client
            // Note: This example assumes the Netty server is running on the same machine as the client
            // If the server is running on a different machine, replace "localhost" with the server's IP address
            // Also, ensure the client and server are using the same secret key to encrypt and decrypt messages

            // Uncomment the following lines to connect to a different server
            String serverAddress = "localhost";
            int serverPort = 8080;

            LOG.info("Connecting to localhost:8080...");
            ChannelFuture future = bootstrap.connect(serverAddress,serverPort).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("Error while running Netty client: " + e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
            LOG.info("Netty client shutdown completed.");
        }
    }



    /**
     * Simple Netty client handler for handling connection and communication.
     */
    private static class SimpleClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(@NotNull ChannelHandlerContext ctx) {
            LOG.info("Netty client connected. Sending message...");
            ctx.writeAndFlush("Hello from AIPOWER CLIENT!");
        }

        @Override
        public void exceptionCaught(@NotNull ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("Exception in Netty client: " + cause.getMessage(), cause);
            ctx.close();
        }
    }
}