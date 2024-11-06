package com.sopotek.aipower.service;

import org.jetbrains.annotations.NotNull;
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

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    // Basic ChatGPT response
    public String getChatGPTResponse(String prompt) {
        return callChatGPT(List.of(Map.of("role", "user", "content", prompt)));
    }

    // Multiple prompt responses
    public List<String> getResponsesForMultiplePrompts(@NotNull List<String> prompts) {
        return prompts.stream().map(this::getChatGPTResponse).collect(Collectors.toList());
    }

    // ChatGPT response with a system message
    public String getChatGPTResponseWithSystemMessage(String prompt, String systemMessage) {
        return callChatGPT(List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", prompt)
        ));
    }

    // ChatGPT response with specific role
    public String getChatGPTResponseWithRole(String prompt, String role) {
        return callChatGPT(List.of(Map.of("role", role, "content", prompt)));
    }

    private String callChatGPT(List<Map<String, String>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");  // Specify the model
        requestBody.put("messages", messages);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("choices")) {
            Map<String, Object> firstChoice = (Map<String, Object>) ((List<?>) responseBody.get("choices")).get(0);
            return (String) ((Map<String, Object>) firstChoice.get("message")).get("content");
        }
        return null;
    }
}
