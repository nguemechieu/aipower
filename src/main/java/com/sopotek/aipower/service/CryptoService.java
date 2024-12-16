package com.sopotek.aipower.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CryptoService {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "https://api.coingecko.com/api/v3/";

    public CryptoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Get current price
    public List <Object>getCryptoPrice(String coinId, String currency) {
        String url = BASE_URL + "/simple/price?ids=" + coinId + "&vs_currencies=" + currency;
        return Collections.singletonList(restTemplate.getForObject(url, Map.class));
    }

    // Get market data for a cryptocurrency
    public List<Object> getCryptoMarketData(String coinId, String currency) {
        String url = BASE_URL +   coinId +currency+ "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";
        return Collections.singletonList(restTemplate.getForObject(url, Map.class));
    }

    // Get historical price data for the past number of days
    public List <Object>getCryptoHistoricalData(String coinId, String currency, int days) {
        String url = BASE_URL + "coins/" + coinId + "/market_chart?vs_currency=" + currency + "&days=" + days;
        return Collections.singletonList(restTemplate.getForObject(url, Map.class));
    }

    // List all available cryptocurrencies
    public List<?> listAvailableCryptos() {
        String url = BASE_URL + "coins/list";
        JsonNode res = restTemplate.getForObject(url, JsonNode.class);
        if (res == null) {
            throw new RuntimeException("Error fetching cryptocurrency data");
        }
        if (res.get("data") == null) {
            throw new RuntimeException("Error fetching cryptocurrency data");
        }
        if (!res.get("data").isArray()) {
            throw new RuntimeException("Error fetching cryptocurrency data");
        }
        return Collections.<Object>singletonList(res.get("data"));

    }

    // Get global cryptocurrency market data summary
    public List<Object> getGlobalMarketSummary() {
        String url = BASE_URL + "global";
        return Collections.singletonList(restTemplate.getForObject(url, Map.class));
    }
}