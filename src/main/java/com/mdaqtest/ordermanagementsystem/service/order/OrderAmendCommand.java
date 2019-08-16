package com.mdaqtest.ordermanagementsystem.service.order;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class OrderAmendCommand {

    private final long orderId;
    private final BigDecimal amount;

    public OrderAmendCommand(long orderId, BigDecimal amount) {
        this.orderId = checkNotNull(orderId);
        this.amount = checkNotNull(amount);
    }

    public long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
