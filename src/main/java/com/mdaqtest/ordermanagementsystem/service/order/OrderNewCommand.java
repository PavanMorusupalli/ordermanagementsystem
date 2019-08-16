package com.mdaqtest.ordermanagementsystem.service.order;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class OrderNewCommand {

    private long orderId;
    private final String securityId;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final String direction;
    private final String accountNumber;

    public OrderNewCommand(long orderId, String securityId, BigDecimal quantity,
                           BigDecimal price, String direction,
                           String accountNumber) {
        this.orderId = checkNotNull(orderId);
        this.securityId = checkNotNull(securityId).toString();
        this.quantity = checkNotNull(quantity);
        this.price = checkNotNull(price);
        this.direction = direction;
        this.accountNumber = checkNotNull(accountNumber);
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

    public BigDecimal getPrice() {
        return price;
    }

    public String getDirection() {
        return direction;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
