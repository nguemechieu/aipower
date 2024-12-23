package com.sopotek.aipower.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
public class NettyClient {

    private static final Log LOG = LogFactory.getLog(NettyClient.class);
    @Value("${server.address}")
    private String serverAddress1="localhost";
    @Value("${server.port}")
    private int serverPort1=8080;

    public void startNettyClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(@NotNull Channel channel) {
                            channel.pipeline().addLast(new SimpleClientHandler());
                        }
                    });

            LOG.info("Attempting to connect to server...");
            ChannelFuture future = bootstrap.connect(serverAddress1, serverPort1).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOG.error("Netty client failed to connect: " + e.getMessage());
        } finally {
            group.shutdownGracefully();
            LOG.info("Netty client shutdown.");
        }
    }


    /**
     * Simple Netty client handler for handling connection and communication.
     */
    static class SimpleClientHandler extends ChannelInboundHandlerAdapter {
        private static final Log logger = LogFactory.getLog(SimpleClientHandler.class);
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            logger.info("Netty client connected. Sending message...");
            ctx.writeAndFlush("Hello from AIPOWER CLIENT!");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("Exception in Netty client: " + cause.getMessage(), cause);
            ctx.close();
        }
        @Override
        public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
            logger.info("Received message from server: " + msg);
        }
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void channelInactive(@NotNull ChannelHandlerContext ctx) {
            logger.info("Netty client disconnected.");
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
        }

    }
}
