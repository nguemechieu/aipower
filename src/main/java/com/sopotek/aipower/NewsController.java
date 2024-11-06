package com.sopotek.aipower;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Properties;
@RestController
@RequestMapping("/api/v3")
public class NewsController {
    // Inside your NewsController class
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    String apiKey;
    Properties properties = new Properties();

    public NewsController() throws IOException {
        objectMapper.configure(
                com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
                true
        );
        this.properties.load(getClass().getClassLoader().getResourceAsStream("./application.properties"));
        this.apiKey = properties.getProperty("news.api.key");
        logger.info(
                this.toString()
        );
    }

    // Existing endpoints...
    @Operation(summary = "Fetches Forex Factory calendar data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched Forex Factory calendar successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/forex-factory")
    public ResponseEntity<?> getForexFactoryCalendar() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://nfs.faireconomy.media/ff_calendar_thisweek.json?version=a603b210ca0358c2414daec0c5a1247b"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return ResponseEntity.status(response.statusCode()).body("Failed to fetch Forex Factory calendar: " + response.body());
            }

            // Parse the JSON data into a List of ForexEvent
            List<ForexFactoryCalendar.ForexEvent> forexEvents = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, ForexFactoryCalendar.ForexEvent.class));
            return ResponseEntity.ok(forexEvents);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching Forex Factory calendar: " + e.getMessage());
        }
    }


}
