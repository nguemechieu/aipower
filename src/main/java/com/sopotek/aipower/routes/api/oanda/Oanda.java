package com.sopotek.aipower.routes.api.oanda;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
@Getter
@Setter
@Service
public class Oanda {

    @Value("${oanda.api.account.id}")
    private  String account_id;
    private final RestTemplate restTemplate;

    @Value("${oanda.api.url}")
    private String baseUrl;
@Value("${oanda.api.access.token}")
    private  String apiKey;


    public Oanda() {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "https://api-fxtrade.oanda.com/v3";
        this.account_id="001-001-2783446-002";

    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getApiKey());
        headers.add("Content-Type", "application/json");

        return headers;
    }

    // Function: Get Account Details
    public ResponseEntity<?> getAccountDetails(String accountId) {
        String url = baseUrl + "/accounts/" + accountId;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url,HttpMethod.GET, request, Map.class);
    }

    // Function: Get Instrument Prices
    public ResponseEntity<?> getInstrumentPrices(String instruments) {
        String url = baseUrl + "/pricing?instruments=" + instruments;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    // Function: Place an Order
    public ResponseEntity<?> placeOrder(String accountId, Map<String, Object> orderDetails) {
        String url = baseUrl + "/accounts/" + accountId + "/orders";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderDetails, getHeaders());
        return restTemplate.exchange(url,HttpMethod.POST, request, Map.class);
    }

    // Function: Get Order Details
    public ResponseEntity<?> getOrderDetails(String accountId, String orderId) {
        String url = baseUrl + "/accounts/" + accountId + "/orders/" + orderId;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    // Function: Close a Trade
    public ResponseEntity<?> closeTrade(String accountId, String tradeId) {
        String url = baseUrl + "/accounts/" + accountId + "/trades/" + tradeId + "/close";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
    }

    // Function: Get Account Summary
    public ResponseEntity<?> getAccountSummary(String accountId) {
        String url = baseUrl + "/accounts/" + accountId + "/summary";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    // Function: Get Historical Candles
    public ResponseEntity<?> getHistoricalCandles(String instrument, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/instruments/" + instrument + "/candles");
        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            params.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
            urlBuilder.setLength(urlBuilder.length() - 1); // Remove trailing "&"
        }
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, request, Map.class);
    }

    // Function: Get Open Positions
    public ResponseEntity<?> getOpenPositions(String accountId) {
        String url = baseUrl + "/accounts/" + accountId + "/openPositions";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url,HttpMethod.GET, request, Map.class);
    }

    // Function: Get Trade Details
    public ResponseEntity<?> getTradeDetails(String accountId, String tradeId) {
        String url = baseUrl + "/accounts/" + accountId + "/trades/" + tradeId;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }


    public ResponseEntity<?> getAllInstruments() {

        String url = baseUrl+ "/accounts/" + getAccount_id()+ "/instruments";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(URI.create(url), HttpMethod.GET, request, Map.class);
    }



        // Get a list of Trades for an Account
        public ResponseEntity<?> getTrades(String accountId) {
            String url = baseUrl + "/accounts/" + accountId + "/trades";
            HttpEntity<Void> request = new HttpEntity<>(getHeaders());
            return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        }

        // Get the list of open Trades for an Account
        public ResponseEntity<?> getOpenTrades(String accountId) {
            String url = baseUrl + "/accounts/" + accountId + "/openTrades";
            HttpEntity<Void> request = new HttpEntity<>(getHeaders());
            return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        }

        // Get the details of a specific Trade in an Account


        // Close (partially or fully) a specific open Trade in an Account
        public ResponseEntity<?> closeTrade(String accountId, String tradeSpecifier, Map<String, Object> body) {
            String url = baseUrl + "/accounts/" + accountId + "/trades/" + tradeSpecifier + "/close";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());
            return restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        }

        // Update the Client Extensions for a Trade
        public ResponseEntity<?> updateClientExtensions(String accountId, String tradeSpecifier, Map<String, Object> clientExtensions) {
            String url = baseUrl + "/accounts/" + accountId + "/trades/" + tradeSpecifier + "/clientExtensions";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(clientExtensions, getHeaders());
            return restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        }

        // Create, replace, and cancel a Trade’s dependent Orders
        public ResponseEntity<?> manageTradeOrders(String accountId, String tradeSpecifier, Map<String, Object> orders) {
            String url = baseUrl + "/accounts/" + accountId + "/trades/" + tradeSpecifier + "/orders";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(orders, getHeaders());
            return restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        }
    }
