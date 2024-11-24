package com.sopotek.aipower.network.stellar;

import lombok.*;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.TradeAggregationResponse;
import org.stellar.sdk.xdr.TransactionV0Envelope;

import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Component

public class StellarClient {
    private static final Logger LOG = LoggerFactory.getLogger(StellarClient.class);
    private String account;   // Stellar account ID
    private String balance;   // Account balance
    private String currency;  // Currency type (e.g., XLM)
    private static final String HORIZON_URL = "https://horizon.stellar.org"; // Public Stellar network URL
    private static final Server server = new Server(HORIZON_URL);
    private @NonNull Asset selling;
    private @NonNull Asset buying;
    private TransactionV0Envelope envelope;
    private AccountResponse sourceAccount;
    private AccountResponse accountResponse;
    private KeyPair sourceKeyPair;
    private Transaction transaction;
    private Page<TradeAggregationResponse> tradeAggregations;
    okhttp3.OkHttpClient HttpClient=new OkHttpClient();
    okhttp3.OkHttpClient SubmitHttpClient=new OkHttpClient();
    public StellarClient(String account) throws IOException {
        this.account = account;
        server.setHttpClient(HttpClient);
        server.setSubmitHttpClient(SubmitHttpClient);
        fetchAccountDetails(account);
    }

    /**
     * Fetches account details from the Stellar network.
     *
     * @param accountId Stellar account ID
     * @throws IOException in case of network issues
     */
    public void fetchAccountDetails(String accountId) throws IOException {
        try {
            accountResponse = server.accounts().account(accountId);
            this.account = accountId;
            this.balance = accountResponse.getBalances()[0].getBalance();
            this.currency = accountResponse.getBalances()[0].getAssetCode().isEmpty() ? "XLM" : accountResponse.getBalances()[0].getAssetCode().get();
        } catch (Exception e) {
            LOG.error("Failed to fetch account details: {}", e.getMessage(), e);
            throw new IOException("Failed to fetch account details: " + e.getMessage(), e);
        }
    }

    /**
     * Places a trade (buy/sell offer) on the Stellar DEX.
     *
     * @param sourceSecretKey Secret key of the source account placing the trade.
     * @param sellingAsset    The asset being sold.
     * @param buyingAsset     The asset being bought.
     * @param amount          Amount to trade.
     * @param price           Price per unit.
     */
    public void placeTrade(String sourceSecretKey, Asset sellingAsset, Asset buyingAsset, String amount, String price) {
        try {
             sourceKeyPair = KeyPair.fromSecretSeed(sourceSecretKey);
             sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
            transaction  = new TransactionBuilder(sourceAccount,Network.PUBLIC).build();
            transaction.sign(sourceKeyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);

            if (response.isSuccess()) {
                LOG.info("Trade placed successfully: {}", response.getLedger());
            } else {
                LOG.error("Trade placement failed: {}", response.getExtras().getResultCodes());
            }
        } catch (Exception e) {
            LOG.error("Error placing trade: {}", e.getMessage(), e);
        }
    }

    /**
     * Closes an existing trade (cancels an offer).
     *
     * @param sourceSecretKey Secret key of the source account.
     */
    public void closeTrade(String sourceSecretKey) {
        try {
          sourceKeyPair = KeyPair.fromSecretSeed(sourceSecretKey);
           sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
            sourceKeyPair = KeyPair.fromSecretSeed(sourceSecretKey);

            Transaction transaction  = new TransactionBuilder(sourceAccount,Network.PUBLIC).build();
            transaction.sign(sourceKeyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);


            if (response.isSuccess()) {
                LOG.info("Trade closed successfully: {}", response.getLedger());
            } else {
                LOG.error("Failed to close trade: {}", response.getExtras().getResultCodes());
            }
        } catch (Exception e) {
            LOG.error("Error closing trade: {}", e.getMessage(), e);
        }
    }

    /**
     * Fetches OHLC market data using Stella's trade aggregations.
     *
     * @param baseAsset   Base asset in the trade pair.
     * @param counterAsset Counter asset in the trade pair.
     * @param resolution   Time resolution in milliseconds (e.g., 300000 for 5 minutes).
     * @param startTime    Start time in milliseconds since epoch.
     * @param endTime      End time in milliseconds since epoch.
     */
    public void fetchOHLCData(Asset baseAsset, Asset counterAsset, long resolution, long startTime, long endTime) {
        try {
             tradeAggregations = server.tradeAggregations(
                            baseAsset,
                            counterAsset,
                            startTime,
                            endTime,
                            resolution, 0)
                    .execute();

            tradeAggregations.getRecords().forEach(aggregation -> LOG.info("OHLC: Open={}, High={}, Low={}, Close={}, Volume={}",
                    aggregation.getOpen(),
                    aggregation.getHigh(),
                    aggregation.getLow(),
                    aggregation.getClose(),
                    aggregation.getBaseVolume()));
        } catch (Exception e) {
            LOG.error("Error fetching OHLC data: {}", e.getMessage(), e);
        }
    }

    /**
     * Displays the StellarClient details in a readable format.
     */
    public void displayDetails() {
        LOG.info("Stellar Account: {}", account);
        LOG.info("Balance: {} {}", balance, currency);
    }
}
