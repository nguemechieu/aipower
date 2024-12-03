package com.sopotek.aipower.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class Coinbase {

    private static final Logger LOG = LoggerFactory.getLogger(Coinbase.class);

    @Value("${COINBASE_API_URL}")
    private String apiUrl;

    @Value("${COINBASE_API_KEY}")
    private String apiKey;

    @Value("${COINBASE_API_SECRET}")
    private String apiSecret;

    @Value("${COINBASE_API_PASSPHRASE}")
    private String passphrase;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        LOG.info("Coinbase service initialized with API URL: {}", apiUrl);
    }

    /**
     * Generates a Coinbase API authentication header.
     *
     * @param method  HTTP method (e.g., "GET", "POST").
     * @param endpoint API endpoint path.
     * @param body     Request body (can be empty for GET).
     * @return A map containing the headers.
     */
    private @NotNull @Unmodifiable Map<String, String> generateAuthHeaders(String method, String endpoint, String body) {
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String prehash = timestamp + method.toUpperCase() + endpoint + body;

            // Decode API secret
            byte[] secretDecoded = Base64.getDecoder().decode(apiSecret);

            // Create HMAC-SHA256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secretDecoded, "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(prehash.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hash);

            // Return headers
            return Map.of(
                    "CB-ACCESS-KEY", apiKey,
                    "CB-ACCESS-SIGN", signature,
                    "CB-ACCESS-TIMESTAMP", timestamp,
                    "CB-ACCESS-PASSPHRASE", passphrase,
                    "Content-Type", "application/json",

                    "Accept", "application/json",
                    "Accept-Charset", "utf-8",
                    "Accept-Language", "en-US",
                    "Connection", "keep-alive",
                    "User-Agent", "AIPower"
            );
        } catch (Exception e) {
            LOG.error("Error generating Coinbase authentication headers: {}", e.getMessage());
            throw new RuntimeException("Failed to generate authentication headers", e);
        }
    }

    /**
     * Fetch user accounts from Coinbase API.
     *
     * @return A map of account data.
     * @throws JsonProcessingException if response parsing fails.
     */
    public Map<String, Object> fetchAccounts() throws JsonProcessingException {
        String endpoint = "/accounts";
        try {
            URI uri = URI.create(apiUrl + endpoint);
            String response = executeAuthenticatedRequest("GET", String.valueOf(uri), "");
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (RestClientException e) {
            LOG.error("Error fetching accounts: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Fetch OHLCV (candlestick) data for a specific product.
     *
     * @param productId   The product ID (e.g., "BTC-USD").
     * @param start       Start time in ISO 8601 format (e.g., "2023-01-01T00:00:00Z").
     * @param end         End time in ISO 8601 format (e.g., "2023-01-02T00:00:00Z").
     * @param granularity Granularity in seconds (e.g., 60, 300, 3600, etc.).
     * @return A list of OHLCV data arrays.
     * @throws JsonProcessingException if response parsing fails.
     */
    public List<List<Object>> getOHLCV(String productId, String start, String end, int granularity) throws JsonProcessingException {
        String endpoint = String.format("/products/%s/candles?start=%s&end=%s&granularity=%d",
                productId, start, end, granularity);
        try {
            String response = executeAuthenticatedRequest("GET", endpoint, "");
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (RestClientException e) {
            LOG.error("Error fetching OHLCV data: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Fetch current prices for a list of cryptocurrencies.
     *
     * @param currencies List of currency pairs (e.g., ["BTC-USD", "ETH-USD"]).
     * @return A map of prices for the given currencies.
     */
    public Map<String, Object> getCurrentPrices(List<String> currencies) {
        String endpoint = "/products";
        try {
            String response = restTemplate.getForObject(apiUrl + endpoint, String.class);
            List<Map<String, Object>> products = objectMapper.readValue(response, new TypeReference<>() {});
            return products.stream()
                    .filter(product -> currencies.contains(product.get("id").toString()))
                    .collect(Collectors.toMap(product -> product.get("id").toString(), product -> product.get("price")));
        } catch (RestClientException | JsonProcessingException e) {
            LOG.error("Error fetching current prices: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch current prices", e);
        }
    }

    /**
     * Executes an authenticated request to Coinbase API.
     *
     * @param method  HTTP method (e.g., "GET", "POST").
     * @param endpoint API endpoint path.
     * @param body     Request body (can be empty for GET).
     * @return API response as a String.
     */
    private String executeAuthenticatedRequest(String method, String endpoint, String body) {
        try {
            Map<String, String> headers = generateAuthHeaders(method, endpoint, body);
            return restTemplate.execute(
                    endpoint,
                    org.springframework.http.HttpMethod.valueOf(method),
                    request -> headers.forEach(request.getHeaders()::add),
                    response -> new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            LOG.error("Error executing authenticated request: {}", e.getMessage());
            throw new RuntimeException("Failed to execute authenticated request", e);
        }
    }
}
