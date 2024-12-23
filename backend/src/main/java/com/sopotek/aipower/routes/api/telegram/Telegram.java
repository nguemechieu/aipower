package com.sopotek.aipower.routes.api.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Getter
@Setter

@Component
public class Telegram {
    private static final Logger logger = LoggerFactory.getLogger(Telegram.class);
    private static final String TELEGRAM_BASE_URL = "https://api.telegram.org";

@Value("${spring.boot.admin.notify.telegram.auth-token:2032573404:AAGnxJpNMJBKqLzvE5q4kGt1cCGF632bP7A}")
    private String botToken;

    protected final RestTemplate restTemplate = new RestTemplate();

    public Telegram( ) {



    }

    // Send a screenshot
    public boolean sendScreenshot(long chatId, File screenshotFile) {
        if (screenshotFile == null || !screenshotFile.exists()) return false;

        String url = TELEGRAM_BASE_URL + "/bot" + getBotToken()+ "/sendPhoto";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create multipart payload
            var filePart = new FileSystemResource(screenshotFile);
            var body = new LinkedMultiValueMap<String, Object>();
            body.add("chat_id", chatId);
            body.add("photo", filePart);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send POST request
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
           logger.error(e.getMessage(), e);
            return false;
        }
    }
    // Send a text message
    public boolean sendMessage(long chatId, String text, boolean asHtml, boolean silently) {
        if (text == null || text.isEmpty()) return false;

        String url = TELEGRAM_BASE_URL + "/bot" +getBotToken() + "/sendMessage";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build payload
        var payload = new MessagePayload(chatId, text, asHtml ? "HTML" : null, silently);
        HttpEntity<MessagePayload> requestEntity = new HttpEntity<>(payload, headers);

        // Send POST request
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    // Get bot updates
    public JsonNode getUpdates(long offset) {
        String url = TELEGRAM_BASE_URL + "/bot" + getBotToken() + "/getUpdates?offset=" + offset;
        return restTemplate.getForObject(url, JsonNode.class);
    }

    // Example: Get bot info
    public JsonNode getMe() {
        String url = TELEGRAM_BASE_URL + "/bot" + getBotToken() + "/getMe";
        return restTemplate.getForObject(url, JsonNode.class);
    }

    // DTO for message payload
    @Data
    @AllArgsConstructor
    static class MessagePayload {
        private long chat_id;
        private String text;
        private String parse_mode;
        private boolean disable_notification;
    }
}
