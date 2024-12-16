//package com.sopotek.aipower.routes.api.coinbase;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.sopotek.aipower.service.exchange.Coinbase;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Date;
//
//@RestController
//@RequestMapping("/coinbase")
//public class CoinbaseController {
//
//    private final Coinbase coinbase;
//
//    @Autowired
//    public CoinbaseController(Coinbase coinbase) {
//        this.coinbase = coinbase;
//    }
//
//    @GetMapping("/accounts")
//    public ResponseEntity<?> getAccounts() throws JsonProcessingException {
//        return ResponseEntity.ok( coinbase.fetchAccounts());
//    }
//
//    // Get OHCLV
//    @GetMapping("/ohlcv")
//    public ResponseEntity<?>getOHLCV() throws JsonProcessingException {
//        return
//                ResponseEntity.ok(coinbase.getOHLCV("BTC/USDC",
//                        String.valueOf(new Date().getTime() - (1000 * 60 * 60 * 24 * 7)) // 7 days ago in milliseconds
//                        ,new Date().toString(),3600));
//    }
//}
