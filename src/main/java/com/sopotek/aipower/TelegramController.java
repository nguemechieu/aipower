package com.sopotek.aipower;


import com.sopotek.aipower.service.TelegramService;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@RestController ("/telegram")
public class TelegramController {

    private final TelegramService telegramService=new TelegramService();


    public TelegramController() throws IOException {

    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long chatId, @RequestParam String message) {
        telegramService.sendMessage(chatId, message);
        return "Message sent to chatId: " + chatId;
    }

    @PutMapping("/edit")
    public String editMessage(@RequestParam Long chatId, @RequestParam Integer messageId, @RequestParam String newText) {
        telegramService.editMessage(chatId, messageId, newText);
        return "Message with ID " + messageId + " edited in chatId: " + chatId;
    }

    @DeleteMapping("/delete")
    public String deleteMessage(@RequestParam Long chatId, @RequestParam Integer messageId) {
        telegramService.deleteMessage(chatId, messageId);
        return "Message with ID " + messageId + " deleted from chatId: " + chatId;
    }

    @GetMapping("/botInfo")
    public Object getBotInfo() throws TelegramApiException {

        return telegramService.getBotInfo();
    }
}