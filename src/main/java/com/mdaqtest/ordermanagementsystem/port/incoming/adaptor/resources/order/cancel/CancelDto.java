package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.cancel;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class CancelDto {

    @JsonProperty(access = READ_ONLY)
    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
