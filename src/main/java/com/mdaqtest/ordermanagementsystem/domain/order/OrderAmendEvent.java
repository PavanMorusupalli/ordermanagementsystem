package com.mdaqtest.ordermanagementsystem.domain.order;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Order Amend/Modify Event class.
 */
public class OrderAmendEvent extends Event
{
    private final String securityId;
    private final BigDecimal quantity;
    private final BigDecimal leavesQuantity;
    private final BigDecimal price;
    private final Order.Direction direction;
    private final String accountNumber;
    private final Order.Request request;
    private final OrderAmendSubType orderState;

    public static final String TYPE = "ORDER_AMEND";

    public OrderAmendEvent(long orderId, DateTime timestamp, int version,
                           String securityId, BigDecimal quantity,
                           BigDecimal leavesQuantity, BigDecimal price,
                           Order.Direction direction, String accountNumber,
                           Order.Request request) {
        super(orderId, timestamp, version);
        this.securityId = checkNotNull(securityId).toString();
        this.quantity = checkNotNull(quantity);
        this.leavesQuantity = checkNotNull(leavesQuantity);
        this.price = checkNotNull(price);
        this.direction = direction;
        this.accountNumber = checkNotNull(accountNumber);
        this.request = request;
        this.orderState = validateOrder(quantity, leavesQuantity, price,
                securityId) ? OrderAmendSubType.AMEND_ACCEPTED : OrderAmendSubType.AMEND_REJECTED;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderAmendSubType getOrderState() {
        return orderState;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public enum OrderAmendSubType {
        AMEND_ACCEPTED, AMEND_REJECTED
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
