package com.sopotek.aipower.service;




import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class TelegramService extends TelegramLongPollingBot {
    final Logger LOG = Logger.getLogger(TelegramService.class.getName());

    String botUsername;
    String botToken;

    public TelegramService() throws IOException {
        botUsername = "AiPower TelegramBot";
        Properties configuration= new Properties();
        configuration.load(
                TelegramService.class.getClassLoader().getResourceAsStream("./application.properties")
        );


        this.botToken=configuration.getProperty("telegram.bot.token");
    }

    @Override
    public String getBotUsername() {
        try {
            return getMe().getUserName();
        } catch (TelegramApiException e) {
//
            LOG.info("Telegram API error: " + e.getMessage());
            return botUsername;
        }


    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String responseText = "You said: " + message.getText();

            SendMessage response = new SendMessage();
            response.setChatId(message.getChatId().toString());
            response.setText(responseText);

            try {
                execute(response); // Send a response message to Telegram
            } catch (TelegramApiException e) {
                LOG.info("Telegram API error: " + e.getMessage());

            }
        }
    }

    public void sendMessage(@NotNull Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {

            LOG.info("Telegram API error: " + e.getMessage());
        }
    }

    public void editMessage(@NotNull Long chatId, Integer messageId, String newText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(newText);
        message.setReplyToMessageId(messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.info("Telegram API error" + e.getMessage());
        }
    }


    public void deleteMessage(@NotNull Long chatId, Integer messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setReplyToMessageId(messageId);

        InlineKeyboardMarkup replyMarkup;
        replyMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton("Delete message");
        button.setCallbackData("delete");

        List<List<InlineKeyboardButton>> keyboards =
                new ArrayList<>();
        keyboards.add(List.of(button));
        replyMarkup.setKeyboard(keyboards);

        message.setReplyMarkup(replyMarkup);
        message.setText("Do you want to delete this message?");
        message.setReplyToMessageId(messageId);
        message.setChatId(chatId.toString());


        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.info("Telegram API error: " + e.getMessage());
        }
    }

    public Object getBotInfo() throws TelegramApiException {

        User getme = getMe();

        String Info = "Bot username: " + getme.getUserName() + "\n" +
                "Bot token: " + getBotToken() + "\n" +
                "Bot first name: " + getme.getFirstName() + "\n" +
                "Bot last name: " + getme.getLastName() + "\n" +
                "Bot id: " + getme.getId() + "\n" +
                "Bot language code: " + getme.getLanguageCode() + "\n" +
                "Bot is bot: " + getme.getIsBot() + "\n" +
                "Bot can join groups: " + getme.getCanJoinGroups() + "\n" +
                "Bot Can Read All GroupMessages: " + getme.getCanReadAllGroupMessages() + "\n";


        return Info;

    }


}
