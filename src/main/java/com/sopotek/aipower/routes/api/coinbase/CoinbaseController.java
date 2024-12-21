package com.sopotek.aipower.routes.api.coinbase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@RestController
@RequestMapping("/coinbase")
public class CoinbaseController {

    private  Coinbase coinbase;

    public CoinbaseController() {
        this.coinbase = new Coinbase();
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts() {
        return ResponseEntity.ok( coinbase.getAccountInfo());
    }

    // Get OHCLV
    @GetMapping("/candle/{symbol}")
    public ResponseEntity<?>getOHLCV(@PathVariable String symbol) {
        return
                ResponseEntity.ok(coinbase.getOHLCV(symbol,
                        String.valueOf(new Date().getTime() - (1000 * 60 * 60 * 24 * 7)) // 7 days ago in milliseconds
                        ,new Date().toString(),3600));
    }
    // Get account details
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> getAccountDetails(@PathVariable String accountId) {
        return ResponseEntity.ok(coinbase.getAccountDetails(accountId));
    }
    // Get exchange rates
    @GetMapping("/exchange-rates/{currency}")
    public ResponseEntity<?> getExchangeRates(@PathVariable String currency) {
        return ResponseEntity.ok(coinbase.getExchangeRates(currency));
    }
    // Create buy order
    @GetMapping("/accounts/{accountId}/buys")
    public ResponseEntity<?> createBuyOrder(@PathVariable String accountId, @RequestBody Map<String, Object> buyDetails) {
        return ResponseEntity.ok(coinbase.createBuyOrder(accountId, buyDetails));
    }
    // Create sell order
    @GetMapping("/accounts/{accountId}/sells")
    public ResponseEntity<?> createSellOrder(@PathVariable String accountId, @RequestBody Map<String, Object> sellDetails) {
        return ResponseEntity.ok(coinbase.createSellOrder(accountId, sellDetails));
    }
    // Get transaction details
    @GetMapping("/accounts/{accountId}/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(@PathVariable String accountId, @PathVariable String transactionId) {
        return ResponseEntity.ok(coinbase.getTransactionDetails(accountId, transactionId));
    }
    // Get payment methods
    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        return ResponseEntity.ok(coinbase.getPaymentMethods());
    }

    // Get current prices

    @GetMapping("/prices/{currencyPair}/spot")
    public ResponseEntity<?> getCurrentPrices(@PathVariable String currencyPair) {
        return ResponseEntity.ok(coinbase.getCurrentPrices(currencyPair));
    }
    // Withdraw funds
    @PostMapping("/accounts/{accountId}/withdrawals")
    public ResponseEntity<?> withdrawFunds(@PathVariable String accountId, @RequestBody Map<String, Object> withdrawalDetails) {
        return ResponseEntity.ok(coinbase.withdrawFunds(accountId, withdrawalDetails));
    }














}
