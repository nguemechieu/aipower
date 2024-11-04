package com.sopotek.aipower;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Properties;

@RestController
@RequestMapping("/api/v3")
public class NewsController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    String apiKey ; // Keep this secure
    Properties properties = new Properties();
    public NewsController() throws IOException {
        objectMapper.configure(
                com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
                true
        );
        this. properties.load(getClass().getClassLoader().getResourceAsStream("./application.properties"));
        this.apiKey=properties.getProperty("news.api.key");

    }

    @Operation(summary = "Fetches news articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched news successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    @GetMapping("/news")
    public ResponseEntity<?> getNews() {
        try {
            String date = LocalDate.now(ZoneId.of("UTC")).minusDays(1).toString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://newsapi.org/v2/everything?q=tesla&from=" + date + "&sortBy=publishedAt&apiKey=" + apiKey))
                    .GET()
                    .build();

            return getResponseEntity(request);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching news: " + e.getMessage());
        }
    }

    // Forex factory news
    @GetMapping("/forex")
    public ResponseEntity<?> getForexNews() {
        try {
            String date = LocalDate.now(ZoneId.of("UTC")).minusDays(1).toString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://newsapi.org/v2/everything?q=forex&from=" + date + "&sortBy=publishedAt&apiKey=" + apiKey))
                    .GET()
                    .build();

            return getResponseEntity(request);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching forex news: " + e.getMessage());
        }
    }

    private @NotNull ResponseEntity<?> getResponseEntity(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return ResponseEntity.status(response.statusCode()).body("Failed to fetch news: " + response.body());
        }

        News news = objectMapper.readValue(response.body(), News.class);
        return ResponseEntity.status(200).body(news);
    }
}