package com.sopotek.aipower.routes.api.binanceus;

import com.sopotek.aipower.component.Binanceus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/binanceus")
public class BinanceusController {

    private static final Logger LOG = LoggerFactory.getLogger(BinanceusController.class);
    public static final String TAG = "BinanceusController";

    private final Binanceus binanceus;

    @Autowired
    public BinanceusController(Binanceus binanceus) {
        this.binanceus = binanceus;
    }

    @GetMapping("/trading-pairs")
    public ResponseEntity<?> getAllTradingPairs() {
        LOG.info("{}: Fetching all trading pairs", TAG);
        return binanceus.getAllTradingPairs()
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error fetching trading pairs: {}", e.getMessage()))
                .block();
    }

    @GetMapping("/ticker/{symbol}")
    public ResponseEntity<?> getTicker(@PathVariable String symbol) {
        LOG.info("{}: Fetching ticker for symbol {}", TAG, symbol);
        return binanceus.getTicker(symbol)
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error fetching ticker for {}: {}", symbol, e.getMessage()))
                .block();
    }

    @GetMapping("/24hr-stats")
    public ResponseEntity<?> get24HourPriceChangeStats() {
        LOG.info("{}: Fetching 24-hour price change stats", TAG);
        return binanceus.get24HourPriceChangeStats()
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error fetching 24-hour stats: {}", e.getMessage()))
                .block();
    }

    @GetMapping("/order-book/{symbol}")
    public ResponseEntity<?> getOrderBook(@PathVariable String symbol) {
        int limit=1000;
        LOG.info("{}: Fetching order book for symbol {} with limit {}", TAG, symbol, limit);
        return binanceus.getOrderBook(symbol, limit)
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error fetching order book: {}", e.getMessage()))
                .block();
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(
            @RequestParam String symbol,
            @RequestParam String side,
            @RequestParam String type,
            @RequestParam String quantity,
            @RequestParam String price) {
        LOG.info("{}: Placing order for symbol {}", TAG, symbol);
        return binanceus.placeOrder(symbol, side, type, quantity, price)
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error placing order: {}", e.getMessage()))
                .block();
    }
// getAggregateTrades
    @GetMapping("/aggregate-trades/{symbol}")
    public ResponseEntity<?> getAggregateTrades(@PathVariable String symbol) {
        int fromId=0;
        int limit=1000;
        LOG.info("{}: Fetching aggregate trades for symbol {} with fromId {} and limit {}", TAG, symbol, fromId, limit);
        return binanceus.getAggregateTrades(symbol, fromId, limit)
               .map(ResponseEntity::ok)
               .doOnError(e -> LOG.error("Error fetching aggregate trades: {}", e.getMessage()))
               .block();
    }
    //Get open orders
    @GetMapping("/open-orders/{symbol}")
    public ResponseEntity<?> getOpenOrders(@PathVariable String symbol) {
        LOG.info("{}: Fetching open orders for symbol {}", TAG, symbol);
        return binanceus.getOpenOrders(symbol)
               .map(ResponseEntity::ok)
               .doOnError(e -> LOG.error("Error fetching open orders: {}", e.getMessage()))
               .block();
    }
    //Get the best price
    @GetMapping("/best-price/{symbol}/{side}")
    public ResponseEntity<?> getBestPrice(@PathVariable String symbol, @PathVariable String side) {
        LOG.info("{}: Fetching best price for symbol {} with side {}", TAG, symbol, side);
        return binanceus.getBestPrice(symbol, side)
               .map(ResponseEntity::ok)
               .doOnError(e -> LOG.error("Error fetching best price: {}", e.getMessage()))
               .block();
    }

    @DeleteMapping("/cancel-order")
    public ResponseEntity<?> cancelOrder(
            @RequestParam String symbol,
            @RequestParam long orderId) {
        LOG.info("{}: Cancelling order with ID {} for symbol {}", TAG, orderId, symbol);
        return binanceus.cancelOrder(symbol, orderId)
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error cancelling order: {}", e.getMessage()))
                .block();
    }

    @GetMapping("/server-time")
    public ResponseEntity<?> getServerTime() {
        LOG.info("{}: Fetching server time", TAG);
        return binanceus.getServerTime()
                .map(ResponseEntity::ok)
                .doOnError(e -> LOG.error("Error fetching server time: {}", e.getMessage()))
                .block();
    }

    @GetMapping("/account-info")
    public ResponseEntity<?> getAccountInfo() throws NoSuchAlgorithmException {
        LOG.info("{}: Fetching account info for symbol ", TAG);
        return binanceus.getAccountInfo().block();}
}
