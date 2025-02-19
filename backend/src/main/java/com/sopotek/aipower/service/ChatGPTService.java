package com.sopotek.aipower.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatGPTService {

    private static final Logger logger = LoggerFactory.getLogger(ChatGPTService.class);

    private final RestTemplate restTemplate=new RestTemplate();

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    public ChatGPTService() {

    }

    public String getChatGPTResponse(String prompt) {
        validatePrompt(prompt);
        return callChatGPT(List.of(Map.of("role", "user", "content", prompt)));
    }

    public List<String> getResponsesForMultiplePrompts(@NotNull List<String> prompts) {
        return prompts.stream().map(this::getChatGPTResponse).collect(Collectors.toList());
    }

    public String getChatGPTResponseWithSystemMessage(String prompt, String systemMessage) {
        validatePrompt(prompt);
        return callChatGPT(List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", prompt)
        ));
    }

    public String getChatGPTResponseWithRole(String prompt, String role) {
        validatePrompt(prompt);
        return callChatGPT(List.of(Map.of("role", role, "content", prompt)));
    }

    private @Nullable String callChatGPT(List<Map<String, String>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            logger.info("Sending request to ChatGPT with messages: {}", messages);
            ResponseEntity<?> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String,Object> responseBody = (Map<String, Object>) response.getBody();
            logger.info("Received response: {}", responseBody);

            if (responseBody != null && responseBody.containsKey("choices")) {
                List<?> choices = (List<?>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = (Map<String, Object>) choices.getFirst();
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        return (String) message.get("content");
                    }
                }
            }
            return "Unexpected response format from ChatGPT";

        } catch (Exception e) {
            logger.error("Error while communicating with ChatGPT: {}", e.getMessage());
            return "Error communicating with ChatGPT: " + e.getMessage();
        }
    }

    private void validatePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
    }
}
