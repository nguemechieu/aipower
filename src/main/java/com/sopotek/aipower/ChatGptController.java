package com.sopotek.aipower;


import com.sopotek.aipower.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController("/chat")
public class ChatGptController {

    @Autowired
    ChatGPTService chatGPTService;

    // Basic chat endpoint with single prompt
    @GetMapping("/message")
    public ResponseEntity<String> chat(@RequestParam String prompt) {
        return ResponseEntity.ok(chatGPTService.getChatGPTResponse(prompt));
    }

    // Chat endpoint for multiple prompts
    @PostMapping("/multi-prompt")
    public List<String> chatWithMultiplePrompts(@RequestBody List<String> prompts) {
        return chatGPTService.getResponsesForMultiplePrompts(prompts);
    }

    // Chat with system prompt (e.g., setting a context or instructions)
    @GetMapping("/system")
    public String chatWithSystemPrompt(@RequestParam String prompt, @RequestParam String systemMessage) {
        return chatGPTService.getChatGPTResponseWithSystemMessage(prompt, systemMessage);
    }

    // Chat with user-defined roles (e.g., assistant, user, etc.)
    @GetMapping("/role")
    public String chatWithRole(@RequestParam String prompt, @RequestParam String role) {
        return chatGPTService.getChatGPTResponseWithRole(prompt, role);
    }
}