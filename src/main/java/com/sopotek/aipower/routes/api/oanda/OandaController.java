package com.sopotek.aipower.routes.api.oanda;

import lombok.Getter;
import lombok.Setter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Getter
@Setter
@RestController
@RequestMapping("/oanda")
public class OandaController {

    private static final Log LOG = LogFactory.getLog(OandaController.class);

   private Oanda oandaApiClient;
    public OandaController() {
        LOG.info("Initializing OandaController");
        // Initialize Oanda API client here
       this.oandaApiClient = new Oanda();

    }

    // Controller methods for Oanda API calls

    @GetMapping("/instruments")
    public ResponseEntity<?> getInstruments() {
        LOG.info("Fetching Oanda currencies");
        return oandaApiClient.getAllInstruments();
    }

    @GetMapping("/account/summary/{accountId}")
    public ResponseEntity<?> getAccountSummary(@PathVariable String accountId) {
        LOG.info("Fetching Oanda account summary for accountId: {}");
        return oandaApiClient.getAccountSummary(accountId);
    }

    @GetMapping("/accounts/{accountId}")

    public ResponseEntity<?> getAccountDetails(@PathVariable String accountId) {
        LOG.info("Fetching Oanda account details for accountId: {}");
        return oandaApiClient.getAccountDetails(accountId);
    }

    @GetMapping("/accounts/{accountId}/orders")
    public ResponseEntity<?> getOpenOrders(@PathVariable String accountId) {
        LOG.info("Fetching Oanda open orders for accountId: {}");
        return oandaApiClient.getOpenPositions(accountId);
    }

    @GetMapping("/accounts/{accountId}/trades/{tradeId}")
    public ResponseEntity<?> getTradeDetails(@PathVariable String accountId, @PathVariable String tradeId) {
        LOG.info("Fetching Oanda trade details for tradeId: {} in accountId: {}");
        return oandaApiClient.getTradeDetails(accountId, tradeId);
    }

    @GetMapping("/prices/{instruments}")
    public ResponseEntity<?> getInstrumentPrices(@PathVariable String instruments) {
        LOG.info("Fetching Oanda instrument prices for instruments: {}");
        return oandaApiClient.getInstrumentPrices(instruments);
    }

    @PostMapping("/accounts/{accountId}/orders")
    public ResponseEntity<?> placeOrder(@PathVariable String accountId, @RequestBody Map<String, Object> orderDetails) {
        LOG.info("Placing Oanda order for accountId: {} with orderDetails: {}");
        return oandaApiClient.placeOrder(accountId, orderDetails);
    }

    @GetMapping("/accounts/{accountId}/orders/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String accountId, @PathVariable String orderId) {
        LOG.info("Fetching Oanda order details for orderId: {} in accountId: {}");
        return oandaApiClient.getOrderDetails(accountId, orderId);
    }

    @PostMapping("/accounts/{accountId}/trades/{tradeId}/close")
    public ResponseEntity<?> closeTrade(@PathVariable String accountId, @PathVariable String tradeId) {
        LOG.info("Closing Oanda trade tradeId: {} in accountId: {}");
        return oandaApiClient.closeTrade(accountId, tradeId, null);
    }

    // More Oanda API calls can be added here


}
