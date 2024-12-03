package com.sopotek.aipower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopotek.aipower.model.ENUM_SIGNAL;
import com.sopotek.aipower.model.PythonIntegration;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.stellar.sdk.*;
import org.stellar.sdk.Price;
import org.stellar.sdk.exception.TooManyRequestsException;
import org.stellar.sdk.operations.ChangeTrustOperation;
import org.stellar.sdk.operations.ManageBuyOfferOperation;
import org.stellar.sdk.operations.ManageSellOfferOperation;
import org.stellar.sdk.operations.Operation;
import org.stellar.sdk.responses.*;
import org.stellar.sdk.xdr.TransactionV0Envelope;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sopotek.aipower.model.ENUM_SIGNAL.*;

@Getter
@Setter
@AllArgsConstructor

@Service
public class StellarClient {
    private static final Logger LOG = LoggerFactory.getLogger(StellarClient.class);


    private double balance; // Account balance
    private String currency; // Currency type (e.g., XLM)
    @Value("${STELLAR_LUMEN_API_URL}")
    private  String HORIZON_URL="https://horizon.stellar.org" ; // Public Stellar network URL
    private final Server server = new Server(HORIZON_URL);

    private KeyPair sourceKeyPair;
    private AccountResponse accountResponse;
    private TransactionV0Envelope envelope;
    private Transaction transaction;
    private Page<TradeAggregationResponse> tradeAggregations;


    private String publicKey; // Stellar public key

    private String secretKey; // Stellar secret key
    private String exchange;
    private String assetCode;

    @Autowired
    public StellarClient(
        @Value("${stellar.public}")String publicKey,
        @Value("${stellar.secret}")String secretKey) {
        this.sourceKeyPair=KeyPair.fromSecretSeed(secretKey);
        this.accountResponse = server.accounts().account(publicKey);
    startLiveTrade();findTrustedAssetIssuer("BINANCE","USDC");
    }

    /**
     * Initializes the Stellar client.
     */
    @PostConstruct
    public void init() {
        LOG.info("Stellar client initialized with Horizon URL: {}", HORIZON_URL);


    }


    public List<Map<String, String>> getAllAssets() {
        String horizonAssetsUrl = "https://horizon.stellar.org/assets"; // Use Horizon Testnet for testing

        try {
            // Make the API request to Stellar Horizon
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(horizonAssetsUrl, String.class);

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            // Extract asset information
            List<Map<String, String>> assets = new ArrayList<>();
            JsonNode records = rootNode.path("_embedded").path("records");
            for (JsonNode record : records) {
                String code = record.path("asset_code").asText();
                String issuer = record.path("asset_issuer").asText();
                String amount = record.path("amount").asText();

                String type = record.path("asset_type").asText();


                assets.add(Map.of(
                        "code", code,"type",type,
                        "issuer", issuer,
                        "amount", amount
                ));
            }

            return assets;
        } catch (Exception e) {
            LOG.error("Error fetching assets from Stellar ledger: {}", e.getMessage(), e);
            return List.of(Map.of("error", "Failed to fetch assets: " + e.getMessage()));
        }
    }
    public Map<String, String> findTrustedAssetIssuer(String exchange, String assetCode) {

        LOG.info("Searching for trusted issuer for asset: {} on exchange: {}", assetCode, exchange);

        String trustedUrl = "https://api.stellar.expert/explorer/directory?search=" + exchange;

        try {
            // Make the API request to Stellar Expert
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(trustedUrl, String.class);

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // Find the issuer from the response based on asset code
            JsonNode records = rootNode.path("_embedded").path("records");
            for (JsonNode record : records) {
                if (record.has("assets")) {
                    JsonNode assets = record.get("assets");
                    for (JsonNode asset : assets) {
                        if (assetCode.equalsIgnoreCase(asset.get("code").asText())) {
                            String issuer = asset.get("issuer").asText();
                            LOG.info("Found trusted issuer for {} on {}: {}", assetCode, exchange, issuer);
                            return Map.of("assetCode", assetCode, "issuer", issuer);
                        }
                    }
                }
            }

            LOG.warn("No trusted issuer found for asset: {} on exchange: {}", assetCode, exchange);
            return Map.of("error", "No trusted issuer found for asset: " + assetCode);
        } catch (Exception e) {
            LOG.error("Error finding trusted issuer for asset {} on exchange {}: {}", assetCode, exchange, e.getMessage(), e);
            return Map.of("error", "Error finding trusted issuer: " + e.getMessage());
        }
    }
    public Map<String, String> createTrustline(String sourceSecretKey, String assetCode, String issuerPublicKey,int limit) {
        try {
            // Initialize keypair and account response
            KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceSecretKey);
            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

            // Create the asset
            Asset asset = Asset.createNonNativeAsset(assetCode, issuerPublicKey);

        Operation operation = ChangeTrustOperation.builder().
                asset(new ChangeTrustAsset(asset))
                .limit(BigDecimal.valueOf(limit)).build()
                ;
            Transaction transaction = new TransactionBuilder(sourceAccount, Network.PUBLIC)
                    .addOperation(operation)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .setTimeout(180) // Timeout in seconds
                    .build();

            transaction.sign(sourceKeyPair);

            // Submit the transaction
            TransactionResponse response = server.submitTransaction(transaction);

            if (response.getSuccessful()) {
                LOG.info("Trustline created successfully for asset {} issued by {}", assetCode, issuerPublicKey);
                return Map.of(
                        "success", "Trustline created successfully",
                        "ledger", String.valueOf(response.getLedger())
                );
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
     * Fetches account details from the Stellar network.
     */
    public Map<String,String>fetchAccountDetails() {
        String accountId=publicKey;
        try {
            accountResponse = server.accounts().account(accountId);

            this.balance = Double.parseDouble(parseBalances(
                    accountResponse.getBalances().toArray(
                            new AccountResponse.Balance[0]
                    )
            ));
            LOG.info("Fetched account details for account ID: {}", accountId);
            return Map.of("account", accountResponse.toString());
        } catch (Exception e) {
            LOG.error("Failed to fetch account details for {}: {}", accountId, e.getMessage(), e);
            return Map.of("error", "Failed to fetch account details: " + e.getMessage());
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
    AccountResponse sourceAccount ;
    /**
     * Places a trade (buy/sell offer) on the Stellar DEX.

     * @param type            The trade type (BUY or SELL).
     * @param sellingAsset    The asset being sold.
     * @param buyingAsset     The asset being bought.
     * @param amount          Amount to trade.
     * @param price           Price per unit.
     * @return A map with the trade result, including success or error messages.
     */
    public Map<String, String> placeTrade(ENUM_SIGNAL type, Asset sellingAsset, Asset buyingAsset, String amount, String price) {
        try {
            // Initialize source account and key pair

            AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

            // Determine the operation type
            Operation operation;
            if (type == ENUM_SIGNAL.BUY) {
                operation = ManageBuyOfferOperation.builder().buying(buyingAsset)
                        .selling(sellingAsset).offerId(0).amount(new BigDecimal(amount)) .price( Price.fromString(price)).build();

            } else if (type == ENUM_SIGNAL.SELL) {
                operation = ManageSellOfferOperation.builder().selling(sellingAsset)
                        .buying(buyingAsset).offerId(0).amount(new BigDecimal(amount)) .price(Price.fromString(price)).build();

            } else {
                return Map.of("error", "Invalid trade type. Must be BUY or SELL.");
            }

            // Build and sign the transaction
            Transaction transaction = new TransactionBuilder(sourceAccount, Network.PUBLIC)
                    .addOperation(operation)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .setTimeout(180) // Timeout in seconds
                    .build();

            transaction.sign(sourceKeyPair);

            // Submit the transaction
            TransactionResponse response = server.submitTransaction(transaction);

            if (response.getSuccessful()) {
                return Map.of("success", "Trade executed successfully", "ledger", String.valueOf(response.getLedger()));
            } else {
                return Map.of("error", "Trade execution failed", "details", response.getResultXdr());
            }
        } catch (Exception e) {
            LOG.error("Error while placing trade: {}", e.getMessage(), e);
            return Map.of("error", "Failed to execute trade: " + e.getMessage());
        }
    }

    /**
     * Fetches OHLC market data using Stellar's trade aggregations.
     *
     * @param baseAsset   Base asset in the trade pair.
     * @param counterAsset Counter asset in the trade pair.
     * @param resolution   Time resolution in milliseconds (e.g., 300000 for 5 minutes).
     * @param startTime    Start time in milliseconds since epoch.
     * @param endTime      End time in milliseconds since epoch.
     */
    public Map<String,String> fetchOHLCData(Asset baseAsset, Asset counterAsset, long resolution, long startTime, long endTime) throws InterruptedException {



        int retryCount = 0;
        int maxRetries = 5;
        while (retryCount < maxRetries) {
            try {
                // Attempt API request



        try {
            Page<TradeAggregationResponse> tradeAggregations = server.tradeAggregations(
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
} catch (TooManyRequestsException e) {
                retryCount++;
                int waitTime = (int) Math.pow(2, retryCount); // Exponential backoff
                Thread.sleep(waitTime * 1000L);
                System.out.println("Retrying after " + waitTime + " seconds...");
            }
        }
        throw new RuntimeException("Max retries exceeded");
    }



    public Map<String,String> getTransactionDetails() {
        try {
            TransactionResponse transaction = server.transactions().transaction(publicKey);
            LOG.info("Transaction Details: Ledger {}, Account {}, Fee {}, OperationType: {}",
                    transaction.getLedger(),
                    transaction.getSourceAccount(),
                    transaction.getFeeAccount(),
                    transaction.getOperationCount());
            return Map.of("transaction", "Ledger: " + transaction.getLedger() +
                    ", Account: " + transaction.getSourceAccount() +
                    ", Fee: " + transaction.getFeeAccount() +
                    ", Operation Type: " + transaction.getOperationCount());
        } catch (Exception e) {
            LOG.error("Error fetching transaction details: {}", e.getMessage(), e);
            return Map.of("error", "Failed to fetch transaction details: " + e.getMessage());
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
        } else {
            // Replace with correct issuer
            return Asset.createNonNativeAsset(assetCode, "GDUKMGUGDZQK6YH3AFHR2NT4C5OABO5XD66GSW5JHTP6Q2XXQXBDUBB6");
        }
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
    private double[] @Nullable [] preprocessMarketData(Map<String, String> marketData) {
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



    private @NotNull @Unmodifiable Map<String, String> fetchTradeSignal() {
        LOG.info("Fetching trade signal...");

        try {

            
            // Fetch OHLC market data
            Map<String, String> marketData = fetchOHLCData(
                    Asset.createNativeAsset(),
                    Asset.createNonNativeAsset("USDC", "issuer-address"),
                    300000,
                    System.currentTimeMillis() - 86400000,
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
                        "sellingAsset", "USDC",
                        "buyingAsset", "XLM",
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




    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean running = true; // Flag to control the scheduler

    public void startLiveTrade() {
        LOG.info("Starting live trade...");
        scheduler.scheduleAtFixedRate(() -> {
            if (!running) {
                scheduler.shutdown(); // Stop the scheduler when the flag is false
                LOG.info("Live trading stopped.");
                return;
            }

            try {
                // Step 1: Fetch trade signals
                Map<String, String> signal = fetchTradeSignal();
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
        }, 0, 5, TimeUnit.SECONDS); // Schedule task every 5 seconds
    }

    public void stopLiveTrade() {
        running = false;
        scheduler.shutdown();
        LOG.info("Requested live trade shutdown.");
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

        Map<String, String> tradeResult = placeTrade(enumSignal, sellingAsset, buyingAsset, amount, price);
        if (tradeResult.containsKey("error")) {
            LOG.error("Trade failed: {}", tradeResult.get("error"));
        } else {
            LOG.info("Trade executed successfully: {}", tradeResult.get("offer"));
        }
    }


    public ResponseEntity<?> getOffers() {
        try {

            Page<OfferResponse> offers = server.offers().execute();
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

    @Override
    public String toString() {
        return "StellarClient{" +
                "account='" + publicKey + '\'' +
                ", balance='" + balance + '\'' +
                ", currency='" + currency + '\'' +
                ", HORIZON_URL='" + HORIZON_URL + '\'' +
                ", server=" + server +
                ", sourceKeyPair=" + sourceKeyPair +
                ", accountResponse=" + accountResponse +
                ", envelope=" + envelope +
                ", transaction=" + transaction +
                ", tradeAggregations=" + tradeAggregations +
                ", publicKey='" + publicKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
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

            // Build the transaction
            Transaction transaction = new TransactionBuilder(sourceAccount, Network.PUBLIC)
                    .addOperation(operation)
                    .setBaseFee(Transaction.MIN_BASE_FEE)
                    .setTimeout(5000)
                    .build();

            // Sign and submit the transaction
            transaction.sign(sourceKeyPair);
            TransactionResponse response = server.submitTransaction(transaction);

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

    public List<TradeAggregationResponse> getOhclv() {
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
}
