package com.sopotek.aipower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.News;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Service
public class TelegramClient {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramClient.class);
    ChatGPTService chatGPTService=new ChatGPTService();

    @Value("${telegram.bot.token}")
    private String botToken;


    private String apiUrl="https://api.telegram.org/bot"+botToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String botApiBase=apiUrl;
    private ScheduledExecutorService scheduler;

    public TelegramClient() {

    }
    public JsonNode getUpdates() {
        String endpoint = botApiBase + "/getUpdates";
        try {
            String response = restTemplate.getForObject(URI.create(endpoint), String.class);
            return objectMapper.readTree(response);
        } catch (Exception e) {
            LOG.error("Error fetching updates from Telegram API at {}: {}", endpoint, e.getMessage(), e);
            return null;
        }
    }


    @PostConstruct
    public void init() {
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("Telegram bot token is not configured");
        }

        this.botApiBase = apiUrl + "/bot" + botToken;
        this.running = true;

        LOG.info("Telegram client initialized with base URL: {}", botApiBase);

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

 
    /**
     * Send a message to a specific chat.
     *
     * @param chatId  The chat ID to send the message to.
     * @param message The message text.
     */
    public void sendMessage(String chatId, String message) {
        String endpoint = botApiBase + "/sendMessage";
        try {
            Map<String, String> payload = Map.of("chat_id", chatId, "text", message);
            restTemplate.postForObject(URI.create(endpoint), payload, String.class);
            LOG.info("Message sent to chat {}: {}", chatId, message);
        } catch (Exception e) {
            LOG.error("Error sending message: {}", e.getMessage());
        }
    }
private String chatId;

    boolean running = false; // Control flag for graceful exit
    /**
     * Process incoming updates and handle messages.
     */
    public void processUpdates() {


        while (running) {
            try {
                JsonNode updates = getUpdates();
                if (updates == null || !updates.has("result")) {
                    LOG.warn("No updates found");
                    sleepFor(5000); // Avoid spamming requests
                    continue;
                }

                updates.get("result").forEach(update -> {
                    if (update.has("message")) {
                        JsonNode message = update.get("message");
                         chatId = message.get("chat").get("id").asText();
                        String text = message.has("text") ? message.get("text").asText() : "";

                        handleCommand(chatId, text);
                    }
                });

                sleepFor(1000); // Prevent rapid polling
            } catch (Exception e) {
                LOG.error("Error processing updates: {}", e.getMessage());
                sleepFor(5000); // Wait before retrying to avoid excessive error logs
            }
        }
    }


    /**
     * Handles user commands and sends appropriate responses.
     *
     * @param chatId The chat ID.
     * @param text   The user message text.
     */
    private void handleCommand(String chatId, @NotNull String text) {
        switch (text.split(" ")[0]) { // Use the first word as the command
            case "/start" -> sendMessage(chatId, "Welcome! Use /help to see available commands.");
            case "/help" -> sendMessage(chatId, getHelpMessage());
            case "/trade" -> handleTrade(chatId);
            case "/news" -> handleMarketNews(chatId);
            case "/portfolio" -> handlePortfolio(chatId);
            case "/settings" -> handleSettings(chatId);
            case "/deposit", "/withdraw", "/balance", "/deposit-history", "/withdraw-history" -> handleFinanceCommand(chatId, text);
            case "/convert", "/rate" -> handleConversion(chatId, text);
            case "/analysis" -> handleAnalysis(chatId);
            case "/chat-gpt" -> handleChatGPT(chatId);
            default -> sendMessage(chatId, "You said: " + text);
        }
    }

    private void handleTrade(String chatId) {
        // Trade logic to select exchange, trade pair, and execute
        sendMessage(chatId, "Trade command received. Please provide the trade details.");
    }



    private void handlePortfolio(String chatId) {
        // Fetch portfolio details
        sendMessage(chatId, "Fetching your portfolio...");
        // Simulate portfolio details
        sendMessage(chatId, "Portfolio: [BTC: 0.5, ETH: 2.0]");
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

    private void sleepFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
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
    private @NotNull List<News> fetchMarketNews() {

        List <News>result = restTemplate.getForObject(
"https://nfs.faireconomy.media/ff_calendar_thisweek.json?version=e670b7d3bd02427fbbfd29d55a0e78ef"

                , List.class
        );
     if (result==null) return new ArrayList<>();
     return result;

    }

}
