package com.sopotek.aipower.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sopotek.aipower.routes.telegram.TelegramController;
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
    private final TelegramController telegramController;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TelegramBotScheduler(TelegramClient telegramClient, TelegramController telegramController) {
        this.telegramClient = telegramClient;
        this.telegramController = telegramController;
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
        JsonNode updates = telegramClient.getUpdates();
        if (updates == null || !updates.has("result")) {
            LOG.warn("No updates found or invalid response.");
            return;
        }

        updates.get("result").forEach(update -> {
            try {
                if (update.has("message")) {
                    JsonNode message = update.get("message");
                    String chatId = message.get("chat").get("id").asText();
                    String text = message.has("text") ? message.get("text").asText() : "";
                    telegramController.processCommand(chatId, text);
                }
            } catch (Exception e) {
                LOG.error("Error processing update: {}", e.getMessage());
            }
        });
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
