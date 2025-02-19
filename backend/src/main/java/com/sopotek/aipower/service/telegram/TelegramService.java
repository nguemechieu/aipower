package com.sopotek.aipower.service.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class TelegramService {

@Autowired
    TelegramClient telegramClient;


    public TelegramService() {

        // Initialize command handlers

    }


}
