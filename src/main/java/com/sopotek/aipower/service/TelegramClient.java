package com.sopotek.aipower.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.News;
import com.sopotek.aipower.model.Telegram;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Service
public class TelegramClient extends Telegram {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramClient.class);
    ChatGPTService chatGPTService=new ChatGPTService();



    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ScheduledExecutorService scheduler;
    private String chatId;

    public TelegramClient() {

    }



    @PostConstruct
    public void init() {


        this.running = true;

        LOG.info("Telegram client initialized with base URL: {}", TELEGRAM_BASE_URL);

        // Use a scheduler for update polling
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::processUpdates, 0, 1, TimeUnit.SECONDS); // Poll every second
    }

    @PreDestroy
    public void onShutdown() {
        stopBot();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            LOG.info("Polling scheduler stopped.");
        }
    }
    public void stopBot() {
        this.running = false;
        LOG.info("Telegram bot has been stopped.");
    }





    boolean running = false; // Control flag for graceful exit
    /**
     * Process incoming updates and handle messages.
     */
    public void processUpdates() {


        while (running) {
            try {
                JsonNode updates = getUpdates(0);
                if (updates == null || !updates.has("result")) {
                    if (updates == null || !updates.has("result")) {
                        LOG.warn("No updates found");
                        // sleepFor(5000); // Avoid spamming requests
                        continue;
                    }

                    updates.get("result").forEach(update -> {
                        if (update.has("message")) {
                            JsonNode message = update.get("message");
                            chatId = message.get("chat").get("id").asText();
                            String text = message.has("text") ? message.get("text").asText() : "";

                            try {
                                handleCommand(Long.parseLong(chatId), text);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    sleepFor(); // Prevent rapid polling


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handles user commands and sends appropriate responses.
     *
     * @param chatId The chat ID.
     * @param text   The user message text.
     */
    private void handleCommand(long chatId, @NotNull String text) throws IOException {
        switch (text.split(" ")[0]) { // Use the first word as the command
            case "/start" -> sendMessage(chatId, "Welcome! Use /help to see available commands.",false,false);
            case "/help" -> sendMessage(chatId, text,false,false);
            case "/trade" -> handleTrade(chatId);
            case "/news" -> handleMarketNews(String.valueOf(chatId));
            case "/portfolio" -> handlePortfolio(chatId);
            case "/settings" -> handleSettings(String.valueOf(chatId));
            case "/deposit", "/withdraw", "/balance", "/deposit-history", "/withdraw-history" -> handleFinanceCommand(String.valueOf(chatId), text);
            case "/convert", "/rate" -> handleConversion(String.valueOf(chatId), text);
            case "/analysis" -> handleAnalysis(String.valueOf(chatId));
            case "/chat-gpt" -> handleChatGPT(String.valueOf(chatId));
            default -> sendMessage(String.valueOf(chatId), "You said: " + text);
        }
    }

    private void handleTrade(long chatId) throws IOException {
        // Trade logic to select exchange, trade pair, and execute
        sendMessage(chatId, "Trade command received. Please provide the trade details.",false,false);
    }



    private void handlePortfolio(long chatId) {
        // Fetch portfolio details
        sendMessage(String.valueOf(chatId), "Fetching your portfolio...");
        // Simulate portfolio details
        sendMessage(String.valueOf(chatId), "Portfolio: [BTC: 0.5, ETH: 2.0]");
    }

    public void sendMessage(String chatId, String s) {
        // Send a message to the specified chat
        String method = "sendMessage";
        Map<String, Object> params = Map.of("chat_id", chatId, "text", s);
        String response = restTemplate.postForObject(String.format("%s/%s", TELEGRAM_BASE_URL, method), params, String.class);
        LOG.info("Sent message: {}", s);
        LOG.info("Response: {}", response);
    }

    private void handleSettings(String chatId) {
        // Fetch and send user settings
        sendMessage(chatId, "Fetching your settings...");
        // Simulate sending settings
        sendMessage(chatId, "Settings: [Alerts: ON, Theme: Dark]");
    }

    private void handleFinanceCommand(String chatId, String command) {
        // Handle finance-related commands
        sendMessage(chatId, "Processing command: " + command);
        // Simulate response
        sendMessage(chatId, "Command result: [Example result]");
    }

    private void handleConversion(String chatId, String command) {
        // Handle currency conversion or rate commands
        sendMessage(chatId, "Processing conversion/rate command: " + command);
        // Simulate response
        sendMessage(chatId, "Conversion rate: 1 BTC = $40,000");
    }

    private void handleAnalysis(String chatId) {
        // Provide market analysis
        sendMessage(chatId, "Performing market analysis...");
        // Simulate analysis result
        sendMessage(chatId, "Market Analysis: [Example analysis]");
    }

    private void handleChatGPT(String chatId) {
        // Fetch and send ChatGPT response
        sendMessage(chatId, "ChatGPT: How can I assist you?");
        String chatGPTResponse = chatGPTService.getChatGPTResponse("Hello, ChatGPT! Provide a response.");
        sendMessage(chatId, "ChatGPT says: " + chatGPTResponse);
    }

    @Contract(pure = true)
    private @NotNull String getHelpMessage() {
        return """
                Available commands:
                /start - Start the bot
                /help - Show help
                /trade - Execute a trade
                /news - Get market news
                /portfolio - View your portfolio
                /settings - View your settings
                /deposit - Make a deposit
                /withdraw - Make a withdrawal
                /balance - Check your balance
                /deposit-history - View deposit history
                /withdraw-history - View withdrawal history
                /convert - Convert currencies
                /rate - Get currency rates
                /analysis - Market analysis
                /chat-gpt - Chat with ChatGPT
                """;
    }

    private void sleepFor() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOG.error("Error in sleep: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void handleMarketNews(String chatId) {
        sendMessage(chatId, "Fetching latest market news...");
        try {
            @NotNull List<News> news = fetchMarketNews(); // Simulate or integrate with a news API
            sendMessage(chatId, "Latest market news: " + news);
        } catch (Exception e) {
            LOG.error("Error fetching market news: {}", e.getMessage());
            sendMessage(chatId, "Unable to fetch market news. Please try again later.");
        }
    }

    @Contract(pure = true)
    private @NotNull List<News> fetchMarketNews() throws IOException {

        List <News>result =new ObjectMapper().readTree((JsonParser) restTemplate.getForObject("https://nfs.faireconomy.media/ff_calendar_thisweek.json?version=e670b7d3bd02427fbbfd29d55a0e78ef", List.class));
     if (result==null) return new ArrayList<>();
     return result;

    }

}
