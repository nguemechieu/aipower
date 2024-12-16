package com.sopotek.aipower.component;

import com.sopotek.aipower.service.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TelegramBotScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotScheduler.class);

    private final TelegramClient telegramClient;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TelegramBotScheduler(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;

        startPolling();
    }

    /**
     * Start the polling scheduler.
     */
    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processUpdates();
            } catch (Exception e) {
                LOG.error("Error during bot polling: {}", e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS); // Poll every second
    }

    /**
     * Fetch updates and process commands.
     */
    private void processUpdates() {
     telegramClient.processUpdates();
    }

    /**
     * Graceful shutdown of the scheduler.
     */
    public void stopPolling() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOG.error("Error shutting down scheduler: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        LOG.info("Polling scheduler stopped.");
    }
}
