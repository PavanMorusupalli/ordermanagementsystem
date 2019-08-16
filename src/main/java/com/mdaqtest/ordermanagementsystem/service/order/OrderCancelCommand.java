package com.mdaqtest.ordermanagementsystem.service.order;

public class OrderCancelCommand {

    private final long orderId;

    public OrderCancelCommand(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }
}
