package com.sopotek.aipower.routes.api.binanceus;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class Binanceus {
private static final Logger LOG= LoggerFactory.getLogger(Binanceus.class);

@Value("${binanceus.api.key}")
    private String apiKey;
@Value("${binanceus.api.secret}")
    private String secretKey;

    private ResponseEntity<String> response;

    public Binanceus() {

    }

    public ResponseEntity<String> makeRequest(HttpMethod  method, String path, Map<String,String> parameters) {
        try {
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        // Create entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Add parameters to request
        if (parameters!= null) {
            path = path + "?" + parameters.entrySet().stream()
                   .map(entry -> entry.getKey() + "=" + entry.getValue())
                   .collect(Collectors.joining("&"));
        }


        // Create URI
        URI urls = URI.create("https://api.binance.us/api/v3/" + path);

        // Make request
        RestTemplate restTemplate = new RestTemplate();


            response = restTemplate.exchange(urls, method, entity, String.class);
        } catch (Exception e) {
           LOG.warn(
                   e.getMessage(), e);

        }
        return response;
    }


    //Get all trading pairs
    public ResponseEntity<String> getAllTradingPairs() {
        return makeRequest(HttpMethod.GET, "ticker/allPrices", null);
    }

    //Get ticker for a specific pair
    public ResponseEntity<String> getTicker(String symbol) {
        return makeRequest(HttpMethod.GET, "ticker/price", Map.of("symbol", symbol));
    }

    //Get 24 hour price change statistics for all symbols
    public ResponseEntity<String> get24HourPriceChangeStats() {
        return makeRequest(HttpMethod.GET, "ticker/24hr", null);
    }

    //Get 24 hour volume by market
    public ResponseEntity<String> get24HourVolumeByMarket() {
        return makeRequest(HttpMethod.GET, "ticker/24hrVolume", null);
    }

    //Get depth of the order book
    public ResponseEntity<String> getOrderBook(String symbol, int limit) {
        return makeRequest(HttpMethod.GET, "depth", Map.of("symbol", symbol, "limit", String.valueOf(limit)));
    }

    // Get historical candlestick data
    public ResponseEntity<String> getHistoricalCandlestickData(String symbol, String interval, int limit) {
        return makeRequest(HttpMethod.GET, "klines", Map.of("symbol", symbol, "interval", interval, "limit", String.valueOf(limit)));
    }

    // Get aggregate trades for a specific symbol
    public ResponseEntity<String> getAggregateTrades(String symbol, int fromId, int limit) {
        return makeRequest(HttpMethod.GET, "aggTrades", Map.of("symbol", symbol, "fromId", String.valueOf(fromId), "limit", String.valueOf(limit)));
    }

    // Get current server time
    public ResponseEntity<String> getServerTime() {
        return makeRequest(HttpMethod.GET, "time", null);
    }

    // Get the best price/qty on a buy or sell order
    public ResponseEntity<String> getBestPrice(String symbol, String side) {
        return makeRequest(HttpMethod.GET, "book/price", Map.of("symbol", symbol, "side", side));
    }

    // Get the current open orders on a symbol
    public ResponseEntity<String> getOpenOrders(String symbol) {
        return makeRequest(HttpMethod.GET, "openOrders", Map.of("symbol", symbol));
    }

    // Place a new order
    public ResponseEntity<String> placeOrder(String symbol, String side, String type, String quantity, String price) {
        return makeRequest(HttpMethod.POST, "order", Map.of("symbol", symbol, "side", side, "type", type, "quantity", quantity, "price", price));
    }

    // Cancel an existing order
    public ResponseEntity<String> cancelOrder(String symbol, long orderId) {
        return makeRequest(HttpMethod.DELETE, "order", Map.of("symbol", symbol, "orderId", String.valueOf(orderId)));
    }

    // Test new order
    public ResponseEntity<String> testNewOrder(String symbol, String side, String type, String quantity, String price) {
        return makeRequest(HttpMethod.POST, "order/test", Map.of("symbol", symbol, "side", side, "type", type, "quantity", quantity, "price", price));
    }



}
