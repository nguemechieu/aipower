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

    public static void main(String[] args) {


        // Load environment variables using Dotenv
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        LOG.info("Environment variables loaded successfully.");


        // Start Spring Boot Application
        SpringApplication.run(AipowerServer.class, args);
        // Start Netty Client
        startNettyClient();//Faster than Jakarta servlet with higher performance and scalability

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
                    .handler(new SimpleClientHandler());

            LOG.info("Connecting to localhost:8080...");
            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
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
