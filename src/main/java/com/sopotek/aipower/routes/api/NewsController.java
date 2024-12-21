package com.sopotek.aipower.routes.api;

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
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String url;

    public NewsController() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/forex-factory")
    public ResponseEntity<?> getForexFactoryCalendar() {
        try {
            String forexApiUrl = "https://nfs.faireconomy.media/ff_calendar_thisweek.json?version=a603b210ca0358c2414daec0c5a1247b";
            String response = restTemplate.getForObject(forexApiUrl, String.class);

            if (response == null) {
                logger.error("Failed to fetch Forex Factory calendar");
                return ResponseEntity.status(500).body("Failed to fetch Forex Factory calendar");
            }

            List<News> forexEvents = objectMapper.readValue(
                    response,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, News.class)
            );

            forexEvents = forexEvents.stream()
                    .filter(event -> event.getStartDate().after(new Date()))
                    .limit(5)
                    .sorted(Comparator.comparing(News::getStartDate))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(forexEvents);
        } catch (Exception e) {
            logger.error("Error fetching Forex Factory calendar", e);
            return ResponseEntity.status(500).body("Error fetching Forex Factory calendar: " + e.getMessage());
        }
    }

    @GetMapping("/news")
    public ResponseEntity<?> getNews() {
        try {
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)));

            String newsApiUrl = String.format(
                    "https://newsapi.org/v2/everything?q=bitcoin&from=%s&sortBy=publishedAt&apiKey=%s",
                    date, apiKey
            );

            String response = restTemplate.getForObject(newsApiUrl, String.class);

            if (response == null) {
                logger.error("Failed to fetch news");
                return ResponseEntity.status(500).body("Failed to fetch news");
            }

            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.has("articles")) {
                List<News> newsList = objectMapper.readValue(
                        rootNode.get("articles").toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, News.class)
                );
                return ResponseEntity.ok(newsList);
            } else {
                logger.error("Unexpected JSON structure: {}", response);
                return ResponseEntity.status(500).body("Unexpected JSON structure");
            }
        } catch (Exception e) {
            logger.error("Error fetching news", e);
            return ResponseEntity.status(500).body("Error fetching news: " + e.getMessage());
        }
    }
}
