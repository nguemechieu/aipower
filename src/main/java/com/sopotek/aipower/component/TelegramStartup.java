package com.sopotek.aipower.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class TelegramStartup {

    private final TelegramBotScheduler telegramBotScheduler;
@Autowired
    public TelegramStartup(TelegramBotScheduler telegramBotScheduler) {
        this.telegramBotScheduler = telegramBotScheduler;
    }
}
