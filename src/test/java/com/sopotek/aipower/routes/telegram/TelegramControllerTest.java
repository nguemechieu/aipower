package com.sopotek.aipower.routes.telegram;

import com.sopotek.aipower.service.TelegramClient;
import org.junit.jupiter.api.Test;

public class TelegramControllerTest {

    @Test
    public void testProcessCommand() {
        TelegramClient mockClient = new TelegramClient();
        TelegramController controller = new TelegramController(mockClient);

        // Simulate a "/start" command
        String chatId = "123456";
        String command = "/start";
        controller.processCommand(chatId, command);

        // Simulate an unknown command
        command = "/unknown";
        controller.processCommand(chatId, command);
    }
}
