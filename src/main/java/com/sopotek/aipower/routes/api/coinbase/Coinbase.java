package com.sopotek.aipower.routes.api.coinbase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Getter
@Setter
@Service
public class Coinbase {

    private final RestTemplate restTemplate;

    @Value("${coinbase.api.base-url}")
    private String baseUrl;

    @Value("${coinbase.api.key}")
    private  String apiKey;
    @Value("${coinbase.api.secret}")

    private String apiSecret;

    public Coinbase() {
        this.restTemplate = new RestTemplate();

    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    // Function: Get Account Information
    public ResponseEntity<?> getAccountInfo() {
        String url = baseUrl + "/accounts";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);
    }

    // Function: Get Account Details by ID
    public ResponseEntity<?> getAccountDetails(String accountId) {
        String url = baseUrl + "/accounts/" + accountId;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);
    }

    // Function: Get Exchange Rates
    public ResponseEntity<?> getExchangeRates(String currency) {
        String url = baseUrl + "/exchange-rates?currency=" + currency;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);
    }

    // Function: Create Buy Order
    public ResponseEntity<?> createBuyOrder(String accountId, Map<String, Object> buyDetails) {
        String url = baseUrl + "/accounts/" + accountId + "/buys";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(buyDetails, getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, request, Map.class);
    }

    // Function: Create Sell Order
    public ResponseEntity<?> createSellOrder(String accountId, Map<String, Object> sellDetails) {
        String url = baseUrl + "/accounts/" + accountId + "/sells";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(sellDetails, getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, request, Map.class);
    }

    // Function: Get Transaction Details
    public ResponseEntity<?> getTransactionDetails(String accountId, String transactionId) {
        String url = baseUrl + "/accounts/" + accountId + "/transactions/" + transactionId;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);
    }

    // Function: Get Payment Methods
    public ResponseEntity<?> getPaymentMethods() {
        String url = baseUrl + "/payment-methods";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    // Function: Get Current Prices
    public ResponseEntity<?> getCurrentPrices(String currencyPair) {
        String url = baseUrl + "/prices/" + currencyPair + "/spot";
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    // Function: Withdraw Funds
    public ResponseEntity<?> withdrawFunds(String accountId, Map<String, Object> withdrawalDetails) {
        String url = baseUrl + "/accounts/" + accountId + "/withdrawals";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(withdrawalDetails, getHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
    }


    public ResponseEntity<?>getOHLCV(String symbol, String s, String string, int i) {

        String url = baseUrl + "/products/" + symbol + "/candles?start=" + s + "&end=" + string + "&granularity=" + i;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Map.class);


    }
}
