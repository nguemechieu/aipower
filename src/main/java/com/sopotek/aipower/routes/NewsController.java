package com.sopotek.aipower.routes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.News;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Getter
@Setter
@RestController
@RequestMapping("/api/v3")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String url;

    public NewsController() {
        // Default constructor for Spring
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        logger.info(this.toString());
    }

    // Fetch Forex Factory calendar data
    @Operation(summary = "Fetches Forex Factory calendar data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched Forex Factory calendar successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/forex-factory")
    public ResponseEntity<?> getForexFactoryCalendar() {
        try {


            HttpResponse<String> response = client.send( HttpRequest.newBuilder()
                    .uri(URI.create("https://nfs.faireconomy.media/ff_calendar_thisweek.json?version=a603b210ca0358c2414daec0c5a1247b"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Failed to fetch Forex Factory calendar: {}", response.body());
                return ResponseEntity.status(response.statusCode()).body("Failed to fetch Forex Factory calendar: " + response.body());
            }

            // Parse the JSON data into a List of ForexFactoryCalendar
            List<News> forexEvents = objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, News.class)
            );

            return ResponseEntity.ok(forexEvents);

        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching Forex Factory calendar", e);
            return ResponseEntity.status(500).body("Error fetching Forex Factory calendar: " + e.getMessage());
        }
    }




    @Operation(summary = "Fetches news data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched news successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/news")
    public ResponseEntity<?> getNews() {
        try {

                    // Fetch news for the previous day
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(new java.util.Date().getTime() - (1000 * 60 * 60 * 24)));

                   // Create the HTTP request to fetch news data from News API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                          url+
                                  "?q=bitcoin&from="+date+"&to="+date+"&sortBy=popularity&apiKey="+apiKey

                    ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Failed to fetch news: {}", response.body());
                return ResponseEntity.status(response.statusCode()).body("Failed to fetch news: " + response.body());
            }

            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(response.body());

            // Extract articles node
            if (rootNode.has("articles")) {
                List<News> newsList = objectMapper.readValue(
                        rootNode.get("articles").toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, News.class)
                );
                return ResponseEntity.ok(newsList);
            } else {
                logger.error("Unexpected JSON structure: {}", response.body());
                return ResponseEntity.status(500).body("Unexpected JSON structure");
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching news", e);
            return ResponseEntity.status(500).body("Error fetching news: " + e.getMessage());
        }
    }


}
