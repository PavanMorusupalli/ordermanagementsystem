package com.mdaqtest.ordermanagementsystem.domain;

import org.joda.time.DateTime;
import java.math.BigDecimal;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Event class, which should be extended to create new type of Events.
 */
public abstract class Event
{
    private final long orderId;
    private final DateTime timestamp;
    private final int version;

    public Event(long orderId, DateTime timestamp, int version) {
        this.orderId = checkNotNull(orderId);
        this.timestamp = checkNotNull(timestamp);
        this.version = checkNotNull(version);
    }

    public long getOrderId() {
        return orderId;
    }

    public DateTime getTimestamp() {
        return this.timestamp;
    }

    public int getVersion() {
        return version;
    }

    /**
     * Get the type of Event.
     */
    public String getType() {return null;};

    /**
     * Validation of input commands.
     * Here we do validations on price, quantity and securityId. Failure in
     * validations will result in runtime exception.
     *
     * @param quantity
     * @param leavesQuantity
     * @param price
     * @param securityId
     */
    protected boolean validateOrder(final BigDecimal quantity,
                                    final BigDecimal leavesQuantity,
                                    final BigDecimal price,
                                    final String securityId) {
        if (quantity.doubleValue() < 0.0d  || leavesQuantity.doubleValue() < 0.0d ||
                price.doubleValue() < 0.0d ||
                securityId.isEmpty()       ||
                securityId.isBlank()) {
            return false;
        }
        return true;
    }
}
