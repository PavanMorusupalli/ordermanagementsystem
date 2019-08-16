package com.mdaqtest.ordermanagementsystem.domain.order;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Order Cancel Event class.
 */
public class OrderCancelEvent extends Event
{
    private final String securityId;
    private final BigDecimal quantity;
    private final BigDecimal leavesQuantity;
    private final BigDecimal price;
    private final Order.Direction direction;
    private final String accountNumber;
    private final Order.Request request;

    public static final String TYPE = "ORDER_CANCEL";

    public OrderCancelEvent(long orderId, DateTime timestamp, int version,
                            String securityId, BigDecimal quantity,
                            BigDecimal price, Order.Direction direction,
                            String accountNumber, Order.Request request)
    {
        super(orderId, timestamp, version);
        this.securityId = checkNotNull(securityId).toString();
        this.quantity = checkNotNull(quantity);
        this.leavesQuantity = BigDecimal.ZERO;
        this.price = checkNotNull(price);
        this.direction = direction;
        this.accountNumber = checkNotNull(accountNumber);
        this.request = request;
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

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
