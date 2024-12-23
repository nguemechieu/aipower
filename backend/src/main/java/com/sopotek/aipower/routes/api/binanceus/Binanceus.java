package com.sopotek.aipower.routes.api.binanceus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.domain.AccountInfo;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class Binanceus {
    private static final Logger LOG = LoggerFactory.getLogger(Binanceus.class);

    @Value("${binanceus.api.key}")
    private String apiKey;

    @Value("${binanceus.api.secret}")
    private String secretKey;

    private  WebClient webClient;
    private AccountInfo accountInfo;

    public Binanceus(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.binance.us/api/v3/")
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // Set max size to 10MB
                .build();
    }

    public Mono<String> makeRequest(HttpMethod method, String path, Map<String, String> parameters) {
        try {
            String url = path;
            if (parameters != null && !parameters.isEmpty()) {
                url += "?" + parameters.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
            }

            LOG.info("Making request to BinanceUS API: Method={}, URL={}", method, url);

            return webClient.method(method)
                    .uri(url)
                    .header("X-MBX-APIKEY", apiKey)
                    .retrieve()
                    .bodyToFlux(String.class) // Stream response
                    .reduce("", String::concat) // Concatenate chunks
                    .doOnSuccess(response -> LOG.info("Response received: {}", response))
                    .doOnError(e -> LOG.error("Error while calling BinanceUS API: {}", e.getMessage(), e));
        } catch (Exception e) {
            LOG.error("Unexpected error in BinanceUS API call: {}", e.getMessage(), e);
            throw new BinanceApiException("Error while processing BinanceUS API request", e);
        }
    }

    public Mono<ExchangeInfo> getAllTradingPairs() {
        return makeRequest(HttpMethod.GET, "exchangeInfo", null)
                .flatMap(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        ExchangeInfo exchangeInfo = mapper.readValue(response, ExchangeInfo.class);
                        LOG.info("Parsed ExchangeInfo: {}", exchangeInfo);
                        return Mono.just(exchangeInfo);
                    } catch (Exception e) {
                        LOG.error("Error parsing exchangeInfo response: {}", e.getMessage(), e);
                        return Mono.error(new BinanceApiException("Failed to parse exchangeInfo response", e));
                    }
                });
    }

    public Mono<ResponseEntity<?>> getAccountInfo() throws NoSuchAlgorithmException {
//        # Get HMAC SHA256 signature
//
//        timestamp=`date +%s000`
//
//        api_key=<your_api_key>
//                secret_key=<your_secret_key>
//
//                api_url="https://api.binance.us"
//
//        signature=`echo -n "timestamp=$timestamp" | openssl dgst -sha256 -hmac $secret_key`
//
//        curl -X "GET" "$api_url/api/v3/account?timestamp=$timestamp&signature=$signature" \
//        -H "X-MBX-APIKEY: $api_key"
        Map<String, String> params= new HashMap<>();
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));

                // HMAC-SHA256 signature
        long timestamp=
                Instant.now().atZone(ZoneId.of("UTC")).toEpochSecond() * 1000;
        String signature =
                        java.util.Base64.getEncoder()
                               .encodeToString(
                                        java.security.MessageDigest.getInstance("SHA-256")
                                               .digest((timestamp + "api_key=" + apiKey).getBytes(StandardCharsets.UTF_8)));
        params.put("signature", signature);

        return makeRequest(HttpMethod.GET, "account", params)
                .map(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                     accountInfo = objectMapper.readValue(response, AccountInfo.class);
                        return ResponseEntity.ok(accountInfo);
                    } catch (Exception e) {
                        LOG.error("Error parsing account info: {}", e.getMessage(), e);
                        return ResponseEntity.status(500).body("Failed to parse account information\n"+e.getMessage());
                    }
                })
                .onErrorResume(e -> {
                    LOG.error("Error retrieving account info: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(500).body("Failed to retrieve account information"));
                });
    }


    public Mono<String> getTicker(String symbol) {
        return makeRequest(HttpMethod.GET, "ticker/price", Map.of("symbol", symbol));
    }

    public Mono<String> get24HourPriceChangeStats() {
        return makeRequest(HttpMethod.GET, "ticker/24hr", null);
    }

    public Mono<String> get24HourVolumeByMarket() {
        return makeRequest(HttpMethod.GET, "ticker/24hrVolume", null);
    }

    public Mono<String> getOrderBook(String symbol, int limit) {
        return makeRequest(HttpMethod.GET, "depth", Map.of("symbol", symbol, "limit", String.valueOf(limit)));
    }

    public Mono<String> getHistoricalCandlestickData(String symbol, String interval, int limit) {
        return makeRequest(HttpMethod.GET, "klines", Map.of("symbol", symbol, "interval", interval, "limit", String.valueOf(limit)));
    }

    public Mono<String> getAggregateTrades(String symbol, int fromId, int limit) {
        return makeRequest(HttpMethod.GET, "aggTrades", Map.of(
                "symbol", symbol,
                "fromId", String.valueOf(fromId),
                "limit", String.valueOf(limit)
        ));
    }

    public Mono<String> getServerTime() {
        return makeRequest(HttpMethod.GET, "time", null);
    }

    public Mono<String> getBestPrice(String symbol, String side) {
        return makeRequest(HttpMethod.GET, "book/price", Map.of("symbol", symbol, "side", side));
    }

    public Mono<String> getOpenOrders(String symbol) {
        Map<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        //type
        params.put("type", "LIMIT_MAKER"); // Include type to avoid rate limiting issues.
        // Set a large value to avoid rate limiting issues.
        params.put("recvWindow", "5000"); // Set a large value to avoid rate limiting issues.
        return makeRequest(HttpMethod.GET, "openOrders", params);
    }

    public Mono<String> placeOrder(String symbol, String side, String type, String quantity, String price) {
        return makeRequest(HttpMethod.POST, "order", Map.of(
                "symbol", symbol,
                "side", side,
                "type", type,
                "quantity", quantity,
                "price", price
        ));
    }

    public Mono<String> cancelOrder(String symbol, long orderId) {
        return makeRequest(HttpMethod.DELETE, "order", Map.of("symbol", symbol, "orderId", String.valueOf(orderId)));
    }

    public Mono<String> testNewOrder(String symbol, String side, String type, String quantity, String price) {
        return makeRequest(HttpMethod.POST, "order/test", Map.of(
                "symbol", symbol,
                "side", side,
                "type", type,
                "quantity", quantity,
                "price", price
        ));
    }

    public static class BinanceApiException extends RuntimeException {
        public BinanceApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeInfo {
        private List<SymbolInfo> symbols;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class SymbolInfo {
            private String symbol;
            private String status;
            private String baseAsset;
            private String quoteAsset;
        }
    }
}
