package com.sopotek.aipower.routes.api.telegram;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.function.BiConsumer;

@Controller

public class TelegramController {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramController.class);

    private final TelegramClient telegramClient;

    private final Map<String, BiConsumer<String, String>> commandHandlers;

    public TelegramController(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;

        // Initialize command handlers
        this.commandHandlers = Map.ofEntries(
                Map.entry("/start", this::handleStart),
                Map.entry("/help", this::handleHelp),
                Map.entry("/chart", this::handleChart),
                Map.entry("/sentiments", this::handleSentiments),
                Map.entry("/account", this::handleAccount),
                Map.entry("/balance", this::handleBalance),
                Map.entry("/exchange", this::handleExchange),
                Map.entry("/deposit", this::handleDeposit),
                Map.entry("/withdraw", this::handleWithdraw),
                Map.entry("/reports", this::handleReports),
                Map.entry("/trade", this::handleTrade),
                Map.entry("/news", this::handleNews),
                Map.entry("/portfolio", this::handlePortfolio),
                Map.entry("/settings", this::handleSettings),
                Map.entry("/chat-gpt", this::handleChatGPT)
        );

        LOG.info("TelegramController initialized with command handlers.");
    }

    /**
     * Process incoming commands.
     *
     * @param chatId The chat ID of the user.
     * @param text   The message text (command).
     */
    public void processCommand(String chatId, @NotNull String text) {
        try {
            String command = text.split(" ")[0]; // Extract the first word as the command
            BiConsumer<String, String> handler = commandHandlers.get(command);

            if (handler != null) {
                handler.accept(chatId, text);
            } else {
                handleUnknownCommand(chatId, text);
            }
        } catch (Exception e) {
            LOG.error("Error processing command: {}", e.getMessage());
                  }
    }


    private void handleStart(String chatId, String text) {

        telegramClient.sendMessage(chatId, "Welcome to the bot! Use /help to see available commands.");
    }

    private void handleHelp(String chatId, String text) {
        String helpMessage = """
                Available commands:
                /start - Start the bot
                /help - Show help
                /chart - Get market chart data
                /sentiments - Get market sentiment information
                /account - Get the bot's account
                /balance - Check your account balance
                /exchange - Check your exchange
                /deposit - Deposit funds
                /withdraw - Withdraw funds
                /reports - Get daily, weekly, or monthly reports
                /trade - Execute a trade
                /news - Get market news
                /portfolio - View your portfolio
                /settings - View your settings
                /chat-gpt - Chat with ChatGPT
                """;
        telegramClient.sendMessage(chatId, helpMessage);
    }

    private void handleTrade(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Please provide trade details (e.g., /trade BTC-USD BUY 0.01).");
    }

    private void handleNews(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching latest market news...");
        // Simulate fetching news
        telegramClient.sendMessage(chatId, "News: Bitcoin reaches $50,000!");
    }

    private void handlePortfolio(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching your portfolio...");
        // Simulate portfolio details
        telegramClient.sendMessage(chatId, "Portfolio: BTC: 0.5, ETH: 1.2");
    }

    private void handleSettings(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching your settings...");
        // Simulate settings
        telegramClient.sendMessage(chatId, "Settings: Notifications ON, Theme: Dark");
    }

    private void handleChatGPT(String chatId, @NotNull String text) {
        telegramClient.sendMessage(chatId, "What would you like to ask ChatGPT?");
        String query = text.replace("/chat-gpt", "").trim();
        if (!query.isEmpty()) {
            String response = telegramClient.getChatGPTService().getChatGPTResponse(query);
            telegramClient.sendMessage(chatId, "ChatGPT says: " + response);
        } else {
            telegramClient.sendMessage(chatId, "Please provide a query after /chat-gpt.");
        }
    }

    private void handleChart(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching chart data...");
        // Simulate chart data
        telegramClient.sendMessage(chatId, "Chart: BTC-USD");
    }

    private void handleSentiments(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching market sentiments...");
        // Simulate sentiment analysis
        telegramClient.sendMessage(chatId, "Sentiments: Positive 60%, Negative 40%");
    }

    private void handleAccount(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching account details...");
        // Simulate account information
        telegramClient.sendMessage(chatId, "Account: Sopotek Bot Account, Active since 2023");
    }

    private void handleBalance(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching account balance...");
        // Simulate balance
        telegramClient.sendMessage(chatId, "Balance: $12,345.67");
    }

    private void handleExchange(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching exchange details...");
        // Simulate exchange information
        telegramClient.sendMessage(chatId, "Exchange: Binance");
    }

    private void handleDeposit(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Deposit command received. Please provide deposit details.");
    }

    private void handleWithdraw(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Withdraw command received. Please provide withdrawal details.");
    }

    private void handleReports(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Fetching reports...");
        // Simulate reports
        telegramClient.sendMessage(chatId, "Reports: Weekly Profit: $500, Monthly Profit: $2,000");
    }

    private void handleUnknownCommand(String chatId, String text) {
        telegramClient.sendMessage(chatId, "Unknown command: " + text + ". Use /help to see available commands.");
    }
}
