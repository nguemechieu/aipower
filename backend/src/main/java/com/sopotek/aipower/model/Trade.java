package com.sopotek.aipower.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "trades")
public class Trade  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Trade attributes
    private  String tradeId; // Unique trade identifier
    private  String instrument; // e.g., BTC/USD, EUR/USD
    private TradeType tradeType; // BUY or SELL
    private  OrderType orderType; // MARKET or LIMIT
    private BigDecimal quantity; // Amount of the asset being traded
    private BigDecimal price; // Price per unit (for LIMIT orders)
    private BigDecimal executedPrice; // Actual execution price (for MARKET orders)
    private BigDecimal fee; // Trading fee
    private TradeStatus status; // OPEN, COMPLETED, CANCELLED
    private final LocalDateTime createdAt; // Time the trade was created
    private LocalDateTime executedAt; // Time the trade was executed
    @Id
    private Long id;

    // Constructor
    public Trade(String instrument, TradeType tradeType, OrderType orderType, BigDecimal quantity, BigDecimal price) {
        this.tradeId = UUID.randomUUID().toString(); // Generate unique ID
        this.instrument = instrument;
        this.tradeType = tradeType;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.status = TradeStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Trade() {
        this.tradeId = UUID.randomUUID().toString(); // Generate unique ID
        this.status = TradeStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    // Method to execute the trade
    public void execute(BigDecimal executedPrice) {
        if (this.status != TradeStatus.OPEN) {
            throw new IllegalStateException("Trade is not in an open state.");
        }

        this.executedPrice = executedPrice;
        this.status = TradeStatus.COMPLETED;
        this.executedAt = LocalDateTime.now();
    }

    // Method to cancel the trade
    public void cancel() {
        if (this.status != TradeStatus.OPEN) {
            throw new IllegalStateException("Only open trades can be cancelled.");
        }

        this.status = TradeStatus.CANCELLED;
    }

    // Method to calculate fees (example: fee as percentage of trade value)
    public void calculateFee(BigDecimal feePercentage) {
        if (this.executedPrice != null && this.quantity != null) {
            this.fee = this.executedPrice.multiply(this.quantity).multiply(feePercentage).divide(BigDecimal.valueOf(100));
        } else {
            throw new IllegalStateException("Cannot calculate fees without execution price and quantity.");
        }
    }


    // Enum for TradeType (BUY or SELL)
    public enum TradeType {
        BUY,
        SELL
    }

    // Enum for OrderType (MARKET or LIMIT)
    public enum OrderType {
        MARKET,
        LIMIT,
        STOP_LOSS,
        STOP_LIMIT
    }

    // Enum for TradeStatus (OPEN, COMPLETED, CANCELLED)
    public enum TradeStatus {
        OPEN,
        COMPLETED,
        CANCELLED
    }

    // String representation for debugging
    @Override
    public String toString() {
        return "Trade{" +
                "tradeId='" + tradeId + '\'' +
                ", instrument='" + instrument + '\'' +
                ", tradeType=" + tradeType +
                ", orderType=" + orderType +
                ", quantity=" + quantity +
                ", price=" + price +
                ", executedPrice=" + executedPrice +
                ", fee=" + fee +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", executedAt=" + executedAt +
                '}';
    }
}
