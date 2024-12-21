package com.sopotek.aipower.routes.api.stellar;

import com.fasterxml.jackson.databind.JsonNode;
import com.sopotek.aipower.model.ENUM_SIGNAL;

import com.sopotek.aipower.model.PythonIntegration;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.glxn.qrgen.QRCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.stellar.sdk.*;
import org.stellar.sdk.Price;
import org.stellar.sdk.operations.*;
import org.stellar.sdk.responses.*;
import org.stellar.sdk.responses.operations.OperationResponse;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sopotek.aipower.component.StellarSorobanSmartContract.deploySmartContractXdr;
import static com.sopotek.aipower.model.ENUM_SIGNAL.BUY;
import static com.sopotek.aipower.model.ENUM_SIGNAL.SELL;

/**
 * A service for interacting with the Stellar blockchain.
 */
@Getter
@Setter
@Component
public class StellarClient {
    private static final Logger LOG = LoggerFactory.getLogger(StellarClient.class);
    private static final String HORIZON_MAINNET_URL = "https://horizon.stellar.org";

    private final Server server = new Server(HORIZON_MAINNET_URL);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean running = true;

    @Value("${stellar.public}")
    private String publicKey;

    @Value("${stellar.secret}")
    private String secretKey;

    private KeyPair sourceKeyPair;
    private AccountResponse accountResponse;
    private Map<String, String> tradeResult;
    private Map<String, String> marketData;
    private TransactionResponse transaction;
    private TransactionResponse response;
    private Operation operation;
    private double balance;
    private Page<OfferResponse> offers;
    private Page<AssetResponse> assets;

    public StellarClient() {
    }
    /**
     * Initializes the Stellar client.
     */
    @PostConstruct
    public void init() {
        try {
            this.sourceKeyPair = KeyPair.fromSecretSeed(secretKey);
            this.accountResponse = server.accounts().account(publicKey);
            LOG.info("Stellar client initialized with public key: {}", publicKey);
            deploySmartContractXdr(secretKey);
        } catch (Exception e) {
            LOG.error("Error initializing Stellar client: {}", e.getMessage(), e);
        }
    }

    public void smartContract(String secretKey) throws IOException {
        deploySmartContractXdr(secretKey);
    }

    /**
     * Fetches the account balances.
     * @return Map of asset and balance.
     */
    public Map<String, String> getBalances() {
        try {
            AccountResponse account = server.accounts().account(publicKey);
            Map<String, String> balances = new HashMap<>();
            for (AccountResponse.Balance balance : account.getBalances()) {
                String asset = balance.getAssetType().equals("native") ? "XLM" : balance.getAssetCode();
                balances.put(asset, balance.getBalance());
            }
            LOG.info("Balances fetched successfully for account: {}", publicKey);
            return balances;
        } catch (Exception e) {
            LOG.error("Error fetching balances: {}", e.getMessage(), e);
            return Map.of("error", "Failed to fetch balances: " + e.getMessage());
        }
    }

    /**
     * Creates a trustline for a given asset.
     * @param assetCode The asset code.
     * @param issuerPublicKey The issuer's public key.
     * @param limit The trustline limit.
     * @return Result of the operation.
     */
    public Map<String, String> createTrustline(String assetCode, String issuerPublicKey, BigDecimal limit) {
        try {
            Asset asset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);
            @NonNull ChangeTrustAsset assetd= new ChangeTrustAsset(asset);
            ChangeTrustOperation operation = ChangeTrustOperation.builder()
                    .asset(assetd)
                    .limit(limit)
                    .build();

            Transaction transaction = buildTransaction(List.of(operation));
            transaction.sign(sourceKeyPair);

            response = server.submitTransaction(transaction);
            if (response.getSuccessful()) {
                LOG.info("Trustline created successfully for asset: {}", assetCode);
                return Map.of("success", "Trustline created successfully");
            } else {
                LOG.error("Failed to create trustline: {}", response.getResultXdr());
                return Map.of("error", "Failed to create trustline", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error creating trustline: {}", e.getMessage(), e);
            return Map.of("error", "Error creating trustline: " + e.getMessage());
        }
    }

    /**
     * Places a trade on the Stellar DEX.
     * @param signal Trade signal (BUY/SELL).
     * @param sellingAsset The asset to sell.
     * @param buyingAsset The asset to buy.
     * @param amount The amount to trade.
     * @param price The price per unit.
     * @return Result of the operation.
     */
    public Map<String, String> placeTrade(ENUM_SIGNAL signal, Asset sellingAsset, Asset buyingAsset, BigDecimal amount, String price) {
        try {
          operation = createTradeOperation(signal, sellingAsset, buyingAsset, amount, price);
            Transaction transactions = buildTransaction(List.of(operation));
            transactions.sign(sourceKeyPair);
            response = server.submitTransaction(transactions);
            if (response.getSuccessful()) {
                LOG.info("Trade executed successfully: Ledger {}", response.getLedger());
                return Map.of("success", "Trade executed successfully", "ledger", String.valueOf(response.getLedger()));
            } else {
                LOG.error("Trade failed: {}", response.getResultXdr());
                return Map.of("error", "Trade execution failed", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error placing trade: {}", e.getMessage(), e);
            return Map.of("error", "Failed to execute trade: " + e.getMessage());
        }
    }

    /**
     * Builds a Stellar transaction.
     * @param operations List of operations to include in the transaction.
     * @return A Stellar transaction.
     */
    private Transaction buildTransaction(List<Operation> operations) {
        return new TransactionBuilder(accountResponse, Network.PUBLIC)
                .addOperations(operations)
                .setBaseFee(Transaction.MIN_BASE_FEE)
                .setTimeout(180)
                .build();
    }


    /**
     * Creates a trade operation.
     * @param signal The trade signal.
     * @param sellingAsset The asset to sell.
     * @param buyingAsset The asset to buy.
     * @param amount The amount to trade.
     * @param price The price per unit.
     * @return A Stellar trade operation.
     */
    private Operation createTradeOperation(ENUM_SIGNAL signal, Asset sellingAsset, Asset buyingAsset, BigDecimal amount, String price) {
        Price priceObject = Price.fromString(price);
        if (signal == BUY) {
            return ManageBuyOfferOperation.builder()
                    .buying(buyingAsset)
                    .selling(sellingAsset)
                    .amount(amount)
                    .price(priceObject)
                    .build();
        } else {
            return ManageSellOfferOperation.builder()
                    .selling(sellingAsset)
                    .buying(buyingAsset)
                    .amount(amount)
                    .price(priceObject)
                    .build();
        }
    }



    /**
     * Creates a new offer on the Stellar DEX.
     *
     * @param sellingAsset The asset to sell.
     * @param buyingAsset  The asset to buy.
     * @param amount       The amount to sell.
     * @param price        The price per unit.
     * @return Result of the operation.
     */
    public Map<String, String> createOffer(Asset sellingAsset, Asset buyingAsset, BigDecimal amount, String price) {
        try {
            ManageSellOfferOperation operation = ManageSellOfferOperation.builder()
                    .selling(sellingAsset)
                    .buying(buyingAsset)
                    .amount(amount)
                    .price(Price.fromString(price))
                    .offerId(0) // New offer
                    .build();

            Transaction transactions = buildTransaction(List.of(operation));
            transactions.sign(sourceKeyPair);

             response = server.submitTransaction(transactions);
            if (response.getSuccessful()) {
                LOG.info("Offer created successfully: Ledger {}", response.getLedger());
                return Map.of("success", "Offer created successfully", "ledger", String.valueOf(response.getLedger()));
            } else {
                LOG.error("Failed to create offer: {}", response.getResultXdr());
                return Map.of("error", "Failed to create offer", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error creating offer: {}", e.getMessage(), e);
            return Map.of("error", "Failed to create offer: " + e.getMessage());
        }
    }

    /**
     * Cancels an existing offer on the Stellar DEX.
     *
     * @param offerId The ID of the offer to cancel.
     * @param sellingAsset The asset being sold.
     * @param buyingAsset The asset being bought.
     * @return Result of the operation.
     */
    public Map<String, String> cancelOffer(long offerId, Asset sellingAsset, Asset buyingAsset) {
        try {
            ManageSellOfferOperation operation = ManageSellOfferOperation.builder()
                    .selling(sellingAsset)
                    .buying(buyingAsset)
                    .amount(BigDecimal.ZERO) // Setting amount to 0 cancels the offer
                    .offerId(offerId)
                    .price(Price.fromString("1")) // Arbitrary price for cancel
                    .build();

            Transaction transaction = buildTransaction(List.of(operation));
            transaction.sign(sourceKeyPair);

            TransactionResponse response = server.submitTransaction(transaction);
            if (response.getSuccessful()) {
                LOG.info("Offer canceled successfully: Ledger {}", response.getLedger());
                return Map.of("success", "Offer canceled successfully", "ledger", String.valueOf(response.getLedger()));
            } else {
                LOG.error("Failed to cancel offer: {}", response.getResultXdr());
                return Map.of("error", "Failed to cancel offer", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error canceling offer: {}", e.getMessage(), e);
            return Map.of("error", "Failed to cancel offer: " + e.getMessage());
        }
    }

    /**
     * Updates an existing offer on the Stellar DEX.
     *
     * @param offerId      The ID of the offer to update.
     * @param sellingAsset The asset to sell.
     * @param buyingAsset  The asset to buy.
     * @param newAmount    The new amount to sell.
     * @param newPrice     The new price per unit.
     * @return Result of the operation.
     */
    public Map<String, String> updateOffer(long offerId, Asset sellingAsset, Asset buyingAsset, BigDecimal newAmount, String newPrice) {
        try {
            ManageSellOfferOperation operation = ManageSellOfferOperation.builder()
                    .selling(sellingAsset)
                    .buying(buyingAsset)
                    .amount(newAmount)
                    .price(Price.fromString(newPrice))
                    .offerId(offerId) // Existing offer ID
                    .build();

            Transaction transaction = buildTransaction(List.of(operation));
            transaction.sign(sourceKeyPair);

            TransactionResponse response = server.submitTransaction(transaction);
            if (response.getSuccessful()) {
                LOG.info("Offer updated successfully: Ledger {}", response.getLedger());
                return Map.of("success", "Offer updated successfully", "ledger", String.valueOf(response.getLedger()));
            } else {
                LOG.error("Failed to update offer: {}", response.getResultXdr());
                return Map.of("error", "Failed to update offer", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error updating offer: {}", e.getMessage(), e);
            return Map.of("error", "Failed to update offer: " + e.getMessage());
        }
    }



    public TransactionResponse getTransactionDetails() {
        try {
        transaction = server.transactions().transaction(publicKey);
            LOG.info("Transaction Details: Ledger {}, Account {}, Fee {}, OperationType: {}",
                    transaction.getLedger(),
                    transaction.getSourceAccount(),
                    transaction.getFeeAccount(),
                    transaction.getOperationCount());
            return transaction;


        } catch (Exception e) {
            LOG.error("Error fetching transaction details: {}", e.getMessage(), e);
            return null;

        }
    }

    public Map<String,String> getBalances(String id) {
        try {
            AccountResponse account = server.accounts().account(id);
            StringBuilder balanceInfo = new StringBuilder();
            for (AccountResponse.Balance balance : account.getBalances()) {
                String asset = balance.getAssetType().equals("native") ? "XLM" : balance.getAssetCode();
                balanceInfo.append(String.format("%s: %s ", asset, balance.getBalance()));
            }
            LOG.info("Balances: {}", balanceInfo);
            return Map.of("balances", balanceInfo.toString());
        } catch (Exception e) {
            LOG.error("Error fetching balances: {}", e.getMessage(), e);
            return Map.of("error", "Failed to fetch balances: " + e.getMessage());
        }
    }

    @Contract("_ -> new")
    private @NotNull Asset getAsset(String assetCode) {
        if ("XLM".equalsIgnoreCase(assetCode)) {
            return Asset.createNativeAsset();
        }
            return Asset.createNonNativeAsset(assetCode, "GA5ZSEJYB37JRC5AVCIA5MOP4RHTM335X2KGX3IHOJAPP5RE34K4KZVN");

    }
    private double parseTimestamp(String timestamp) {
        try {
            // Assuming ISO-8601 format. Adjust pattern if needed.
            Instant instant = Instant.parse(timestamp);
            return instant.toEpochMilli() / 1000.0; // Convert milliseconds to seconds for better scaling
        } catch (DateTimeParseException e) {
            LOG.error("Invalid timestamp format: {}", timestamp, e);
            throw new IllegalArgumentException("Invalid timestamp format: " + timestamp, e);
        }
    }
    private double[][] preprocessMarketData(Map<String, String> marketData) {
        try {
            // Extract OHLC data from the market data
            String ohlc = marketData.get("ohlc");
            if (ohlc == null || ohlc.isEmpty()) {
                LOG.warn("No OHLC data available.");
                return null;
            }

            // Assuming ';' separates OHLC records
            String[] records = ohlc.split(";");
            double[][] features = new double[records.length][5]; // Timestamp, Open, High, Low, Close

            for (int i = 0; i < records.length; i++) {
                // Assuming ',' separates fields: Timestamp, Open, High, Low, Close
                String[] values = records[i].split(",");
                if (values.length < 5) {
                    LOG.warn("Skipping invalid OHLC record: {}", records[i]);
                    continue;
                }

                // Parse fields
                features[i][0] = parseTimestamp(values[0]);         // Timestamp
                features[i][1] = Double.parseDouble(values[1]);     // Open
                features[i][2] = Double.parseDouble(values[2]);     // High
                features[i][3] = Double.parseDouble(values[3]);     // Low
                features[i][4] = Double.parseDouble(values[4]);     // Close
                features[i][5] = Long.getLong(values[5]);//volume
            }

            return features;
        } catch (Exception e) {
            LOG.error("Error preprocessing market data: {}", e.getMessage(), e);
            return null;
        }
    }
    public Map<String,String> fetchOHLCData(Asset baseAsset, Asset counterAsset, long resolution, long startTime, long endTime) throws InterruptedException {



        try {
       tradeAggregations = server.tradeAggregations(
                    baseAsset, counterAsset, startTime, endTime, resolution, -6).execute();

            tradeAggregations.getRecords().forEach(aggregation -> LOG.info("OHLC: Open={}, High={}, Low={}, Close={}, Volume={}",
                    aggregation.getOpen(),
                    aggregation.getHigh(),
                    aggregation.getLow(),
                    aggregation.getClose(),
                    aggregation.getBaseVolume()));
            return Map.of("ohlc", "Open: " + tradeAggregations.getRecords().getFirst().getOpen() +
                    ", High: " + tradeAggregations.getRecords().getFirst().getHigh() +
                    ", Low: " + tradeAggregations.getRecords().getFirst().getLow() +
                    ", Close: " + tradeAggregations.getRecords().getFirst().getClose() +
                    ", Volume : " + tradeAggregations.getRecords().getFirst().getBaseVolume() +
                    ", Time: " + new Date(tradeAggregations.getRecords().getFirst().getTimestamp()));
        } catch (Exception e) {
            LOG.error("Error fetching OHLC data: {}", e.getMessage(), e);
            return Map.of("error", "Failed to fetch OHLC data: " + e.getMessage());

        }
    }






    private @NotNull Map<String, String> fetchTradeSignal(Asset buyingAsset, Asset sellingAsset) {
        LOG.info("Fetching trade signal...");

        try {
            int timeframe=
                    600000*60; // 1 minute in milliseconds
            long start_time = System.currentTimeMillis() - timeframe;

            // Fetch OHLC market data
        marketData = fetchOHLCData(
                    buyingAsset,sellingAsset, timeframe,
                    start_time,
                    System.currentTimeMillis()
            );

            if (marketData.containsKey("error")) {
                LOG.error("Error fetching market data: {}", marketData.get("error"));
                return Map.of("action", "HOLD"); // Default to HOLD if market data fails
            }

            // Step 1: Preprocess data
            double[][] ohlcFeatures = preprocessMarketData(marketData);
            if (ohlcFeatures == null) {
                LOG.warn("Insufficient data for signal generation.");
                return Map.of("action", "HOLD");
            }

            // Step 2: Predict trade signal
            ENUM_SIGNAL signal = predictTradeSignal(ohlcFeatures);
            LOG.info("Generated trade signal: {}", signal);

            // Prepare signal details (mock example)
            if (signal == BUY) {
                return Map.of(
                        "action", "BUY",
                        "sellingAsset", "XLM",
                        "buyingAsset", "USDC",
                        "amount", "10",
                        "price", "0.12"
                );
            } else if (signal == SELL) {
                return Map.of(
                        "action", "SELL",
                        "sellingAsset",     sellingAsset.toString(),
                        "buyingAsset", buyingAsset.toString(),
                        "amount", "10",
                        "price", "0.12"
                );
            } else {
                return Map.of("action", "HOLD");
            }
        } catch (Exception e) {
            LOG.error("Error fetching trade signal: {}", e.getMessage(), e);
            return Map.of("action", "HOLD");
        }
    }




    public void startLiveTrade() {
        LOG.info("Starting live trade...");
        scheduler.scheduleAtFixedRate(() -> {
            if (!running) {
                scheduler.shutdown(); // Stop the scheduler when the flag is false
                LOG.info("Live trading stopped.");
                return;
            }
            try {

                // Replace it with actual asset codes
                Asset a1 = Asset.createNativeAsset();
                Asset a2 = Asset.createNonNativeAsset("USDC", "GA4CIZX3QJADGZZKI7HUS6WVHBNIX3EUNUW4MZUDPK5FIW2E6LLVVHGU");

                // Step 1: Fetch trade signals
                Map<String, String> signal = fetchTradeSignal(a1,a2);
                String action = signal.get("action");

                if ("HOLD".equalsIgnoreCase(action)) {
                    LOG.info("No trade signal received. Retrying...");
                    return; // Skip to the next execution
                }

                // Step 2: Extract details from the signal
                Asset sellingAsset = getAsset(signal.get("sellingAsset"));
                Asset buyingAsset = getAsset(signal.get("buyingAsset"));
                String amount = signal.get("amount");
                String price = signal.get("price");

                // Step 3: Execute trade based on the signal
                if ("BUY".equalsIgnoreCase(action)) {
                    infos(action, sellingAsset, buyingAsset, amount, price, BUY);
                } else if ("SELL".equalsIgnoreCase(action)) {
                    infos(action, sellingAsset, buyingAsset, amount, price, SELL);
                } else {
                    LOG.warn("Invalid trade signal action: {}", action);
                }
            } catch (Exception e) {
                LOG.error("Error during live trade: {}", e.getMessage(), e);
            }
        }, 0, 10, TimeUnit.SECONDS); // Schedule task every 5 seconds
    }


    private ENUM_SIGNAL predictTradeSignal(double[][] ohlcFeatures) {
        try {

            // Make predictions (mock example: model outputs a score between -1 and 1)
            @NotNull ENUM_SIGNAL prediction = new PythonIntegration().getPrediction(ohlcFeatures);
            // Return the predicted trade signal
            return prediction;
        } catch (Exception e) {
            LOG.error("Error predicting trade signal: {}", e.getMessage());
            return ENUM_SIGNAL.HOLD; // Default to HOLD on prediction error
        }
    }





    private void infos(String action, Asset sellingAsset, Asset buyingAsset, String amount, String price, ENUM_SIGNAL enumSignal) {
        LOG.info("Trade Signal: Action={}, Selling={}, Buying={}, Amount={}, Price={}",
                action, sellingAsset, buyingAsset, amount, price);

       tradeResult = placeTrade(enumSignal, sellingAsset, buyingAsset, BigDecimal.valueOf(Long.parseLong(amount)), price);
        if (tradeResult.containsKey("error")) LOG.error("Trade failed: {}", tradeResult.get("error"));
        else LOG.info("Trade executed successfully: {}", tradeResult.get("offer"));
    }


    public ResponseEntity<Object> getOffers() {
        try {

           offers = server.offers().execute();
            offers.getRecords().forEach(offer -> LOG.info("Offer: ID={}, Selling={}, Buying={}, Price={}, Amount={}",
                    offer.getId(),
                    offer.getSelling(),
                    offer.getBuying(),
                    offer.getPrice(),
                    offer.getAmount()));
            return ResponseEntity.ok(offers.getRecords());
        } catch (Exception e) {
            LOG.error("Error fetching offers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch offers: " + e.getMessage());
        }

    }



    public Map<String, String> createOffer(Asset selling, Asset buying, String amount, String price) {
        try {
            // Create the operation
            ManageBuyOfferOperation operation = ManageBuyOfferOperation.builder()
                    .buying(buying)
                    .selling(selling)
                    .amount(new BigDecimal(amount))
                    .price(Price.fromString(price))
                    .offerId(0) // Use 0 for a new offer
                    .build();
            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

            // Build the transaction
            Transaction transactions = new TransactionBuilder(sourceAccount, Network.PUBLIC)
                    .addOperation(operation)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .setTimeout(5000)
                    .build();

            // Sign and submit the transaction
            transactions.sign(sourceKeyPair);
            TransactionResponse response = server.submitTransaction(transactions);

            if (response.getSuccessful()) {
                LOG.info("Offer created successfully: Ledger {}", response.getLedger());
                return Map.of("offer", "Offer created successfully: Ledger: " + response.getLedger());
            } else {
                LOG.error("Offer creation failed: {}", response.getResultXdr());
                return Map.of("error", "Offer creation failed: " + response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error creating offer: {}", e.getMessage(), e);
            return Map.of("error", "Failed to create offer: " + e.getMessage());
        }

    }
    private Page<TradeAggregationResponse> tradeAggregations;


    public List<TradeAggregationResponse> getOHCLV() {
        try {
            List<TradeAggregationResponse> candles = getTradeAggregations().getRecords();

            candles.forEach(candle -> LOG.info("Candle:TimeStamp={}, Open={}, High={}, Low={}, Close={}, Volume={}",
                    candle.getTimestamp(),
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getBaseVolume()
            ));
            return candles;
        } catch (Exception e) {
            LOG.error("Error fetching OHLCV data: {}", e.getMessage(), e);
            return new ArrayList<>();
        }

    }

    private @NotNull String parseBalances(AccountResponse.Balance @NotNull [] balances) {
        StringBuilder balanceInfo = new StringBuilder();
        for (AccountResponse.Balance balance : balances) {
            String asset = balance.getAssetType().equals("native") ? "XLM" : balance.getAssetCode();
            balanceInfo.append(String.format("%s: %s ", asset, balance.getBalance()));
        }
        return balanceInfo.toString();
    }
    /**
     * Fetches account details from the Stellar network.
     */
    public AccountResponse fetchAccountDetails() {
        String accountId=publicKey;


        return ResponseEntity.status(200).body(server.accounts().account(accountId)).getBody();



    }

    public Page<AssetResponse> getAllAssets() {
        try {
            assets = server.assets().execute();
            assets.getRecords().forEach(asset -> LOG.info("Asset: Code={}, Type={}, Description={}, Issuer={}",
                    asset.getAssetCode(),
                    asset.getAssetType(),
                    asset.getBalances(),
                    asset.getAssetIssuer()));
            return assets;
        } catch (Exception e) {
            LOG.error("Error fetching all assets: {}", e.getMessage(), e);
            return null;
        }
    }

    public OrderBookResponse getOrderbook() {
        try {
            return  server.orderBook().execute();
        } catch (Exception e) {
            LOG.error("Error fetching orderbook: {}", e.getMessage(), e);
            return null;
        }
    }




    // Send  payment request
    /**
     * Sends a payment to the specified destination address.
     *
     * @param destinationAddress The Stellar address of the recipient.
     * @param assetCode          The code of the asset to send (e.g., XLM).
     * @param amount             The amount of the asset to send.
     * @return A map containing the status and details of the operation.
     */
    public Map<String, String> sendPayment(String destinationAddress, String assetCode, String amount) {
        Map<String, String> operationResponse = new HashMap<>();
        try {
            // Retrieve the destination account to ensure it's valid
            server.accounts().account(destinationAddress);

            // Define the asset
            Asset asset = "XLM".equalsIgnoreCase(assetCode)
                    ? Asset.createNativeAsset()
                    : Asset.createNonNativeAsset(assetCode, publicKey);

            // Create the payment operation

            PaymentOperation paymentOperation = PaymentOperation.builder().amount(BigDecimal.valueOf(Long.parseLong(amount)))
                    .asset(asset)
                    .destination(destinationAddress)
                    .build();

            // Build the transaction
            Transaction transaction = new TransactionBuilder(accountResponse, Network.PUBLIC)
                    .addOperation(paymentOperation)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .setTimeout(180)
                    .build();

            // Sign the transaction
            transaction.sign(sourceKeyPair);

            // Submit the transaction
            TransactionResponse response = server.submitTransaction(transaction);
            if (response.getSuccessful()) {
                LOG.info("Payment sent successfully: Ledger {}", response.getLedger());
                operationResponse.put("success", "Payment sent successfully");
                operationResponse.put("ledger", String.valueOf(response.getLedger()));
            } else {
                LOG.error("Payment failed: {}", response.getResultXdr());
                operationResponse.put("error", "Payment failed");
                operationResponse.put("details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error sending payment: {}", e.getMessage(), e);
            operationResponse.put("error", "Failed to send payment");
            operationResponse.put("details", e.getMessage());
        }
        return operationResponse;
    }


    //Generate QR CODE TO RECEIVE MONEY

    /**
     * Generates a QR code for receiving Stellar payments.
     *
     * @param destinationAddress The Stellar address where the payment will be sent.
     * @param assetCode          The code of the asset to receive (e.g., XLM).
     * @param amount             The amount of the asset to receive.
     * @return A Base64-encoded QR code image as a string.
     */
    public String generateReceiveQRCode(String destinationAddress, String assetCode, String amount) {
        String receiveUrl = String.format("https://stellar.expert/explorer/public/account/%s?asset=%s&amount=%s",
                destinationAddress, assetCode, amount);

        try {
            // Generate QR code as a byte array
            ByteArrayOutputStream qrCodeStream = QRCode.from(receiveUrl)
                    .withSize(500, 500)
                    .stream();

            // Convert QR code byte array to Base64-encoded string
            byte[] qrCodeBytes = qrCodeStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            LOG.error("Error generating QR code: {}", e.getMessage(), e);
            return "Error generating QR code";
        }
    }

//GET ALL LIST OF PAYMENTS
    public   Page<OperationResponse>getPayments() {
        try {

            return server.payments().forAccount(publicKey).execute();
        } catch (Exception e) {
            LOG.error("Error fetching payments: {}", e.getMessage(), e);
            return null;
        }

    }
    private static final String STELLAR_EXPERT_URL = "https://api.stellar.expert/explorer/public/directory?search=exchange";
    private static final String HORIZON_ASSETS_URL = "https://horizon.stellar.org/assets";

    private  RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches a list of exchanges from Stellar Expert API and filters out malicious entries.
     *
     * @return A list of exchanges containing their name and address.
     */
    public List<Map<String, String>> getExchangesFromStellarExpert() {
        List<Map<String, String>> exchanges = new ArrayList<>();
        try {
            JsonNode response = restTemplate.getForObject(STELLAR_EXPERT_URL, JsonNode.class);
            if (response != null) {
                JsonNode records = response.path("_embedded").path("records");
                for (JsonNode record : records) {
                    String name = record.path("name").asText();
                    String address = record.path("address").asText();
                    String type = record.path("type").asText();
                    JsonNode tags = record.path("tags");

                    if ("exchange".equalsIgnoreCase(type) && !isMalicious(name, tags)) {
                        exchanges.add(Map.of(
                                "name", name,
                                "address", address
                        ));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching exchanges from Stellar Expert: " + e.getMessage());
        }
        exchanges.addAll(getExchangesFromHorizon());
        return exchanges;
    }

    /**
     * Checks if an exchange is malicious based on its name or tags.
     *
     * @param name The name of the exchange.
     * @param tags The tags associated with the exchange.
     * @return True if the exchange is malicious or scam-related, otherwise false.
     */
    private boolean isMalicious(@NotNull String name, JsonNode tags) {
        // Check if the name contains keywords like "scam" or "malicious"
        if (name.toLowerCase().contains("scam") || name.toLowerCase().contains("malicious")) {
            return true;
        }

        // Check if tags contain keywords like "scam" or "malicious"
        if (tags.isArray()) {
            for (JsonNode tag : tags) {
                String tagValue = tag.asText().toLowerCase();
                if (tagValue.contains("scam") || tagValue.contains("malicious")) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Fetches a list of all assets from Horizon API and deduces exchanges.
     *
     * @return A list of assets with possible exchange information.
     */
    public List<Map<String, String>> getExchangesFromHorizon() {
        List<Map<String, String>> assets = new ArrayList<>();
        try {
            JsonNode response = restTemplate.getForObject(HORIZON_ASSETS_URL, JsonNode.class);
            if (response != null) {
                JsonNode records = response.path("_embedded").path("records");
                for (JsonNode record : records) {
                    String assetCode = record.path("asset_code").asText();
                    String issuer = record.path("asset_issuer").asText();
                    String amount = record.path("amount").asText();
                    int numAccounts = record.path("num_accounts").asInt();

                    assets.add(Map.of(
                            "asset_code", assetCode.isEmpty() ? "XLM" : assetCode,
                            "issuer", issuer,
                            "amount", amount,
                            "num_accounts", String.valueOf(numAccounts)
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error("Error fetching assets from Horizon: {}", e.getMessage());
        }
        return assets;
    }


}
