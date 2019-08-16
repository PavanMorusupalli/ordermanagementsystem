package com.mdaqtest.ordermanagementsystem.domain.order;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class OrderCreateEvent extends Event
{
    private final String securityId;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final BigDecimal leavesQuantity;
    private final Order.Direction direction;
    private final String accountNumber;
    private final Order.Request request;
    private final OrderCreateSubType orderState;

    public static final String TYPE = "ORDER_CREATE";

    public OrderCreateEvent(long orderId, DateTime timestamp, int version,
                            String securityId, BigDecimal quantity, BigDecimal price,
                            Order.Direction direction, String accountNumber,
                            Order.Request request) {
        super(orderId, timestamp, version);

        this.securityId = checkNotNull(securityId).toString();
        this.quantity = checkNotNull(quantity);
        BigDecimal leavesQuantityTemp = quantity;
        this.price = checkNotNull(price);
        this.direction = direction;
        this.accountNumber = checkNotNull(accountNumber);
        this.request = request;
        this.orderState = validateOrder(quantity, leavesQuantityTemp, price,
                securityId) ?
                OrderCreateSubType.NEW_ACCEPTED :
                OrderCreateSubType.NEW_REJECTED;

        if (this.orderState == OrderCreateSubType.NEW_REJECTED) {
            leavesQuantityTemp = BigDecimal.ZERO;
        }
        this.leavesQuantity = leavesQuantityTemp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderCreateSubType getOrderState() {
        return orderState;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public enum OrderCreateSubType {
        NEW_ACCEPTED, NEW_REJECTED
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

    public Order.Direction getDirection() {
        return direction;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Order.Request getRequest() {
        return request;
    }
}