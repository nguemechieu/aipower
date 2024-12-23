package com.sopotek.aipower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeolocationService {

    private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);

    // Create an HttpClient instance
    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    public Location getUserLocation() throws IOException, InterruptedException {
        // Prepare the URI for the IP info service
        URI uri = URI.create("https://ipinfo.io/json");

        // Build the request with the URI and headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        // Send the HTTP request and get the response
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            // Log the error and rethrow if necessary
            logger.error("Error while sending request: {}", e.getMessage(), e);
            // Restore the interrupted status if the exception is InterruptedException
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw e; // Or handle it gracefully, return a default Location
        }

        // Check if the response status code is 200 (OK)
        if (response.statusCode() == 200) {
            String body = response.body();

            // Use Jackson to parse the JSON response into a Location object
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Location.class);
        } else {
            logger.error("Unexpected response status: {}", response.statusCode());
            // Return a default Location object if the response status is not 200
            return new Location();
        }
    }
}
