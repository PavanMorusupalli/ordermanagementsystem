package com.mdaqtest.ordermanagementsystem.projection.trade;

import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeProjection {

    private final long orderId;
    private final String securityId;
    private final BigDecimal quantity;
    private final BigDecimal leavesQuantity;
    private final BigDecimal price;
    private final String direction;
    private final String accountNumber;
    private final long tradeId;
    private final BigDecimal tradedQuantity;
    private final BigDecimal tradedPrice;
    private final DateTime timestamp;
    private final int version;

    private final TradeState state;

    public TradeProjection(long orderId, String securityId,
                           BigDecimal quantity, BigDecimal leavesQuantity,
                           BigDecimal price, String direction,
                           String accountNumber, long tradeId,
                           BigDecimal tradedQuantity, BigDecimal tradedPrice,
                           TradeState state, DateTime timestamp, int version)
    {
        this.orderId = checkNotNull(orderId);
        this.securityId = checkNotNull(securityId).toString();
        this.quantity = checkNotNull(quantity);
        this.leavesQuantity = checkNotNull(leavesQuantity);
        this.price = checkNotNull(price);
        this.direction = direction;
        this.accountNumber = accountNumber;
        this.tradeId = tradeId;
        this.tradedQuantity = tradedQuantity;
        this.tradedPrice = tradedPrice;
        this.state = state;
        this.timestamp = timestamp;
        this.version = version;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getLeavesQuantity() {
        return leavesQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDirection() {
        return direction;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getTradeId() {
        return tradeId;
    }

    public BigDecimal getTradedQuantity() {
        return tradedQuantity;
    }

    public BigDecimal getTradedPrice() {
        return tradedPrice;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public TradeState getState() {
        return state;
    }

    public enum TradeState {
        NEW, AMEND, COMPLETE, CANCEL
    }
}
