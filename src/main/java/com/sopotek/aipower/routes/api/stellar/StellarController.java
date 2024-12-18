package com.sopotek.aipower.routes.api.stellar;
import com.sopotek.aipower.service.StellarClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;

import java.math.BigDecimal;

import static com.sopotek.aipower.model.ENUM_SIGNAL.SELL;
@RestController
@RequestMapping("/stellar")
public class StellarController {

    private final StellarClient stellarClient;

    @Autowired
    public StellarController(StellarClient stellarClient) {
        this.stellarClient = stellarClient;
    }
    @GetMapping("/accounts")
    public ResponseEntity<?> getAccountDetails() {
        return ResponseEntity.ok( stellarClient.fetchAccountDetails());


    }

    //Get balances
    @GetMapping("/balances")
    public ResponseEntity<?> getBalances() {
        return ResponseEntity.ok(stellarClient.getBalances());
    }

    //GET OHCLV
    @GetMapping("/ohlcv")
    public ResponseEntity<?> getOHLCV() {
        return ResponseEntity.ok(stellarClient.getOHCLV());
    }

   //Get orderbook
    @GetMapping("/orderbook")
    public ResponseEntity<?> getOrderbook() {
        return ResponseEntity.ok(stellarClient.getOrderbook());
    }

    @PostMapping("/trade")
    public ResponseEntity<?> placeTrade(
                                             @RequestParam String sellingAssetCode,
                                             @RequestParam String buyingAssetCode,
                                             @RequestParam String amount,
                                             @RequestParam String price) {
        try {
            Asset selling = new AssetTypeCreditAlphaNum4(sellingAssetCode, "Issuer");
            Asset buying = new AssetTypeCreditAlphaNum4(buyingAssetCode, "Issuer");
            stellarClient.placeTrade(SELL, selling, buying, BigDecimal.valueOf(Long.parseLong(amount)), price);
            return ResponseEntity.ok("Trade placed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to place trade: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionDetails() {


           return ResponseEntity.ok(stellarClient.getTransactionDetails());
    }


    //Get All Assets
    @GetMapping("/assets")
    public ResponseEntity<?> getAssets() {
       return  ResponseEntity.ok(stellarClient.getAllAssets());

    }

    @GetMapping("/balances/{id}")
    public ResponseEntity<?> getBalances(@PathVariable String id) {

          return (ResponseEntity<?>) stellarClient.getBalances(id);


    }

    //Get Offers fetched

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers() {

          return   stellarClient.getOffers();

    }
    //Create Offer
    @PostMapping("/offers/create")
    public ResponseEntity<?> createOffer(
                                             @RequestParam String sellingAssetCode,
                                             @RequestParam String buyingAssetCode,
                                             @RequestParam String amount,
                                              @RequestParam String price) {

            Asset selling = new AssetTypeCreditAlphaNum4(sellingAssetCode, "Issuer");
            Asset buying = new AssetTypeCreditAlphaNum4(buyingAssetCode, "Issuer");
         return ResponseEntity.ok( stellarClient.createOffer(selling, buying, amount, price));


    }
    //Get All payments
    @GetMapping("/payments")
    public ResponseEntity<?> getPayments() {
         return  ResponseEntity.ok(stellarClient.getPayments());
    }

//GET ALL EXCHANGES
    @GetMapping("/exchanges")
    public ResponseEntity<?> getExchanges() {
         return  ResponseEntity.ok(stellarClient.getExchangesFromStellarExpert());
    }




}
