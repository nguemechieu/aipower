package com.sopotek.aipower.service.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
@Component
@Getter
@Setter
public class TelegramClient {
    private static final String API_URL = "https://api.telegram.org/bot";
    @Value("${spring.boot.admin.notify.telegram.auth-token}")
    private String token;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(TelegramClient.class.getName());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Map<String, BiConsumer<Long, String>> commandHandlers = new HashMap<>();
    private final ConcurrentMap<Long, String> lastUserResponses = new ConcurrentHashMap<>();
    private long updateId = 0;

    public TelegramClient() {

        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        initializeCommandHandlers();
        logger.info("Telegram client initialized");
        start();
    }

    public void start() {
        executorService.execute(this::startPolling);
    }

    private volatile boolean running = true;




    public void stopPolling() {
        executorService.shutdown();
    }

    private final BlockingQueue<JsonNode> updateQueue = new LinkedBlockingQueue<>();

    public void startPolling() {
        new Thread(() -> {
            while (true) {
                try {
                    JsonNode updates = getUpdates();
                    updateQueue.put(updates); // Blocks if full
                    processUpdates(updateQueue.take()); // Blocks until an update is available
                } catch (Exception e) {
                    logger.severe("Error polling updates: " + e.getMessage());
                }
            }
        }).start();
    }



    private JsonNode getUpdates() throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(API_URL + token + "/getUpdates")).newBuilder()
                .addQueryParameter("offset", String.valueOf(updateId + 1))
                .build();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return objectMapper.readTree(response.body().string());
        }
    }

    private void processUpdates(JsonNode updates) {
        if (updates.has("result")) {
            for (JsonNode update : updates.get("result")) {
                if (update.has("update_id")) {
                    updateId = update.get("update_id").asLong();
                }
                if (update.has("message")) {
                    processMessage(update.get("message"));
                }
            }
        }
    }

    private void processMessage(JsonNode message) {
        if (message.has("text") && message.has("chat")) {
            String text = message.get("text").asText();
            long chatId = message.get("chat").get("id").asLong();
            processCommand(chatId, text);
        }
    }

    private void initializeCommandHandlers() {
        commandHandlers.put("/start", this::handleStart);
        commandHandlers.put("/help", this::handleHelp);
        commandHandlers.put("/news", this::handleNews);
        commandHandlers.put("/portfolio", this::handlePortfolio);
        commandHandlers.put("/settings", this::handleSettings);
    }

    private void processCommand(long chatId, String text) {
        String command = text.split(" ")[0];
        commandHandlers.getOrDefault(command, this::handleUnknownCommand).accept(chatId, text);
    }

    private void sendMessage(long chatId, String message) {
        if (lastUserResponses.containsKey(chatId) && lastUserResponses.get(chatId).equals(message)) {
            logger.info("Skipping duplicate response for chat " + chatId);
            return;
        }

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(API_URL + token + "/sendMessage")).newBuilder()
                .addQueryParameter("chat_id", String.valueOf(chatId))
                .addQueryParameter("text", message)
                .addQueryParameter("parse_mode", "Markdown")
                .addQueryParameter("disable_web_page_preview", String.valueOf(false))
                .build();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                logger.warning("Failed to send message: " + response.body().string());
            }
        } catch (IOException e) {
            logger.severe("Error sending message: " + e.getMessage());
        }
        lastUserResponses.put(chatId, message);
    }

    private void handleStart(long chatId, String text) {
        sendMessage(chatId, "🤖 Welcome to AI Power! Use /help to see available commands.");
    }

    private void handleHelp(long chatId, String text) {
        String helpMessage = """
                📌 *Available Commands:*
                ➤ /start - Start the bot
                ➤ /help - Show this help message
                ➤ /news - Get latest market news
                ➤ /portfolio - View your portfolio
                ➤ /settings - View your settings
                """;
        sendMessage(chatId, helpMessage);
    }
    @Value("${news.api.key}")
    String new_api_key;

    private void handleNews(long chatId, String text) {
        sendMessage(chatId, "📰 Fetching latest market news...");
        // Fetch news from news API and send it to the chat
        RestTemplate res= new RestTemplate();
        String newsurl=
                // Replace with your news API URL
                "https://newsapi.org/v2/top-headlines?country=us&apiKey="+new_api_key;
        String news = res.getForObject(newsurl, String.class);
        logger.info("Fetched news: " + news);
        sendMessage(chatId, news);
    }

    private void handlePortfolio(long chatId, String text) {
        sendMessage(chatId, "📊 Portfolio: BTC: 0.5, ETH: 1.2");
    }

    private void handleSettings(long chatId, String text) {
        sendMessage(chatId, "⚙️ Settings: Notifications ON, Theme: Dark");
    }

    private void handleUnknownCommand(long chatId, String text) {
        sendMessage(chatId, "⚠️ Unknown command: " + text + ". Use /help to see available commands.");
    }





}
