package com.sopotek.aipower.routes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.News;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController

public class NewsController {

    public static final Logger logger = LoggerFactory.getLogger(NewsController.class);

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

            //Filter upcoming events
            forexEvents = forexEvents.stream()
                    .filter(event -> event.getStartDate().after(new java.util.Date()))
                    .limit(5)
                    .collect(Collectors.toList());

            // Sort events by start date
            forexEvents.sort(Comparator.comparing(News::getStartDate));

            // Map events to a JSON object
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(forexEvents);
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

            // Return the JSON object
            return ResponseEntity.ok(json);



        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching Forex Factory calendar", e);
            return ResponseEntity.status(500).body("Error fetching Forex Factory calendar: " + e.getMessage());
        }
    }


    @GetMapping("/news")
    public ResponseEntity<?> getNews() {
        try {

                    // Fetch news for the previous day
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(new java.util.Date().getTime() - (1000 * 60 * 60 * 24)));

                   // Create the HTTP request to fetch news data from News API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://newsapi.org/v2/everything?" +
                                    "q=bitcoin&" +
                                    "from=" + date + "&" +
                                    "sortBy=publishedAt&" +
                                    "apiKey=" + apiKey

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
