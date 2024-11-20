package com.sopotek.aipower.routes;

import com.sopotek.aipower.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RestControllerAdvice
@ApiResponse

@RequestMapping("/api/v3/crypto")
public class CryptoController {
CryptoService cryptoService;
@Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Operation(summary = "Fetch cryptocurrency price in specified currency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fetched crypto price successfully"),
            @ApiResponse(responseCode = "404", description = "Coin not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/price")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String coinId, @RequestParam String currency) {
        try {
            List<Object> priceData =  cryptoService.getCryptoPrice(coinId, currency);
            return ResponseEntity.ok(priceData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching crypto price: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch market data for a cryptocurrency")
    @GetMapping("/market")
    public ResponseEntity<?> getCryptoMarketData(@RequestParam String coinId, @RequestParam String currency) {
        try {
            List<Object> marketData = cryptoService.getCryptoMarketData(coinId, currency);
            return ResponseEntity.ok(marketData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching market data: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch historical price data for a cryptocurrency")
    @GetMapping("/historical")
    public ResponseEntity<?> getCryptoHistoricalData(@RequestParam String coinId, @RequestParam String currency, @RequestParam int days) {
        try {
            List<Object> historicalData =  cryptoService.getCryptoHistoricalData(coinId, currency, days);
            return ResponseEntity.ok(historicalData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching historical data: " + e.getMessage());
        }
    }

    @Operation(summary = "List all available cryptocurrencies")
    @GetMapping("/list")
    public ResponseEntity<?> listAvailableCryptos() {
        try {
            List<Object> cryptos = cryptoService.listAvailableCryptos();
            return ResponseEntity.ok(cryptos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error listing available cryptocurrencies: " + e.getMessage());
        }
    }

    @Operation(summary = "Get global cryptocurrency market summary")
    @GetMapping("/global")
    public ResponseEntity<?> getGlobalMarketSummary() {
        try {
            List<Object> globalData = cryptoService.getGlobalMarketSummary();
            return ResponseEntity.ok(globalData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching global market summary: " + e.getMessage());
        }
    }
}