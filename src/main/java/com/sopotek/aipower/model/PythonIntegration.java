package com.sopotek.aipower.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PythonIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(PythonIntegration.class);
    private static final String PYTHON_SERVER_URL = "http://localhost:5000";

    /**
     * Fetch predictions from the Python API.
     *
     * @param features A 2D array representing the features for prediction.
     * @return The trade signal as ENUM_SIGNAL.
     */
    public @NotNull ENUM_SIGNAL getPrediction(double[][] features) {
        try {
            // Set up the URL connection
            URL url = URI.create(PYTHON_SERVER_URL + "/predict").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Serialize features as JSON
            Map<String, Object> data = new HashMap<>();
            data.put("features", features);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(data);

            // Send the request and get the response
            StringBuilder response = sendRequest(conn, jsonInputString);

            // Parse the JSON response
            JsonNode responseNode = objectMapper.readTree(response.toString());
            LOG.info("Received response: {}", responseNode.toString());

            // Extract the signal field
            if (responseNode.has("signal")) {
                String signal = responseNode.get("signal").asText();
                LOG.info("Trade signal: {}", signal);
                return ENUM_SIGNAL.valueOf(signal.toUpperCase());
            } else {
                LOG.warn("Signal field missing in response, defaulting to HOLD.");
                return ENUM_SIGNAL.HOLD;
            }
        } catch (Exception e) {
            LOG.error("Error while fetching prediction: {}", e.getMessage(), e);
            return ENUM_SIGNAL.HOLD; // Default to HOLD on error
        }
    }

    /**
     * Send the POST request and retrieve the response.
     *
     * @param conn            HttpURLConnection object.
     * @param jsonInputString JSON payload as a string.
     * @return The response from the server as a StringBuilder.
     * @throws IOException if an error occurs during the connection or I/O operations.
     */
    private static @NotNull StringBuilder sendRequest(@NotNull HttpURLConnection conn, @NotNull String jsonInputString) throws IOException {
        // Send the request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read the response
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response;
        }
    }
}
