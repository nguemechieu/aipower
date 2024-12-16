package com.sopotek.aipower.routes;

import com.sopotek.aipower.service.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RestControllerAdvice


@RequestMapping("/api/v3/crypto")
public class CryptoController {
CryptoService cryptoService;
@Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }


    @GetMapping("/price")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String coinId, @RequestParam String currency) {
        try {
            List<Object> priceData =  cryptoService.getCryptoPrice(coinId, currency);
            return ResponseEntity.ok(priceData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching crypto price: " + e.getMessage());
        }
    }


    @GetMapping("/market")
    public ResponseEntity<?> getCryptoMarketData(@RequestParam String coinId, @RequestParam String currency) {
        try {
            List<Object> marketData = cryptoService.getCryptoMarketData(coinId, currency);
            return ResponseEntity.ok(marketData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching market data: " + e.getMessage());
        }
    }


    @GetMapping("/historical")
    public ResponseEntity<?> getCryptoHistoricalData(@RequestParam String coinId, @RequestParam String currency, @RequestParam int days) {
        try {
            List<Object> historicalData =  cryptoService.getCryptoHistoricalData(coinId, currency, days);
            return ResponseEntity.ok(historicalData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching historical data: " + e.getMessage());
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> listAvailableCryptos() {
        try {
            List<Object> cryptos = cryptoService.listAvailableCryptos();
            return ResponseEntity.ok(cryptos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error listing available cryptocurrencies: " + e.getMessage());
        }
    }


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