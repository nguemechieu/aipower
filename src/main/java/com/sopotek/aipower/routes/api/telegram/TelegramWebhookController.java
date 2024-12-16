package com.sopotek.aipower.routes.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final TelegramController telegramController;

    public TelegramWebhookController(TelegramController telegramController) {
        this.telegramController = telegramController;
    }

    /**
     * Handles Telegram webhook updates.
     * @param update JSON payload from Telegram.
     */
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody JsonNode update) {
        if (update.has("message")) {
            String chatId = update.get("message").get("chat").get("id").asText();
            String text = update.get("message").has("text") ? update.get("message").get("text").asText() : "";
            telegramController.processCommand(chatId, text);
        }
    }
}
