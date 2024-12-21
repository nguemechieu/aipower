package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.service.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

            return ResponseEntity.ok( cryptoService.getCryptoPrice(coinId, currency));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching crypto price: " + e.getMessage());
        }
    }


    @GetMapping("/market")
    public ResponseEntity<?> getCryptoMarketData(@RequestParam String coinId, @RequestParam String currency) {
        try {

            return ResponseEntity.ok( cryptoService.getCryptoMarketData(coinId, currency));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching market data: " + e.getMessage());
        }
    }


    @GetMapping("/historical")
    public ResponseEntity<?> getCryptoHistoricalData(@RequestParam String coinId, @RequestParam String currency, @RequestParam int days) {
        try {

            return ResponseEntity.ok(cryptoService.getCryptoHistoricalData(coinId, currency, days));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching historical data: " + e.getMessage());
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> listAvailableCryptos() {
        try {

            return ResponseEntity.ok(cryptoService.listAvailableCryptos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error listing available cryptocurrencies: " + e.getMessage());
        }
    }


    @GetMapping("/global")
    public ResponseEntity<?> getGlobalMarketSummary() {
        try {
            return ResponseEntity.ok( cryptoService.getGlobalMarketSummary());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching global market summary: " + e.getMessage());
        }
    }
}