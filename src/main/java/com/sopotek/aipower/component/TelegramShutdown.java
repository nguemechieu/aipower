package  com.sopotek.aipower.component;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class TelegramShutdown {

    private final TelegramBotScheduler telegramBotScheduler;

    public TelegramShutdown(TelegramBotScheduler telegramBotScheduler) {
        this.telegramBotScheduler = telegramBotScheduler;
    }

    @PreDestroy
    public void onShutdown() {
        telegramBotScheduler.stopPolling();
    }
}
