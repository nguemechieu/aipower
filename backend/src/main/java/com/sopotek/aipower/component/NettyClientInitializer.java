package com.sopotek.aipower.component;

import com.sopotek.aipower.config.NettyClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NettyClientInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        new Thread(() -> {
            NettyClient nettyClientConfig = new NettyClient();
            nettyClientConfig.startNettyClient();
        }).start();
    }
}
