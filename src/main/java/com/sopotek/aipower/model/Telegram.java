package com.sopotek.aipower.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Component
public class Telegram {
    private static final String TELEGRAM_BASE_URL = "https://api.telegram.org";
    private static final Logger LOG = Logger.getLogger(Telegram.class.getName());
    @Value("${telegram.bot.token}")
    private String botToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Telegram() {

    }

    // Send a text message
    public int sendMessage(long chatId, String text, boolean asHtml, boolean silently) throws IOException {
        if (text == null || text.isEmpty()) return 0;

        String url = TELEGRAM_BASE_URL + "/bot" + botToken + "/sendMessage";
        StringBuilder params = new StringBuilder();
        params.append("chat_id=").append(chatId)
                .append("&text=").append(URLEncoder.encode(text, StandardCharsets.UTF_8));
        if (asHtml) params.append("&parse_mode=HTML");
        if (silently) params.append("&disable_notification=true");

        return postRequest(url, params.toString());
    }

    // Send a chat action
    public int sendChatAction(long chatId, String action) throws IOException {
        String url = TELEGRAM_BASE_URL + "/bot" + botToken + "/sendChatAction";
        String params = "chat_id=" + chatId + "&action=" + action;
        return postRequest(url, params);
    }

    // Get bot updates
    public JsonNode getUpdates(long offset) throws IOException {
        String url = TELEGRAM_BASE_URL + "/bot" + botToken + "/getUpdates";
        String params = "offset=" + offset;
        String response = getRequest(url + "?" + params);
        return objectMapper.readTree(response);
    }

    // Post request helper
    private int postRequest(String urlString, String params) throws IOException {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = params.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            return 0;
        } else {
            System.err.println("HTTP Error: " + responseCode);
            return responseCode;
        }
    }

    // Get request helper
    private String getRequest(String urlString) throws IOException {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    // Example: Get bot info
    public JsonNode getMe() throws IOException {
        String url = TELEGRAM_BASE_URL + "/bot" + botToken + "/getMe";
        String response = getRequest(url);
        return objectMapper.readTree(response);
    }

    // Helper to URL-encode a string
    private String urlEncode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }


}
