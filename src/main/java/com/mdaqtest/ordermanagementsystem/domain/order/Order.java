package com.mdaqtest.ordermanagementsystem.domain.order;

import com.mdaqtest.ordermanagementsystem.domain.Aggregate;
import com.mdaqtest.ordermanagementsystem.domain.Event;

import java.math.BigDecimal;
import java.util.List;

import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;

/**
 * All the operations on Order are handled in this class.
 * Currently we handle Order Creation, Order Amend/Modify and Order Cancel.
 * Order Trade is handled by different object, which listens to Order events
 * on Event Bus.
 */
public class Order extends Aggregate
{
    private String securityId;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal leavesQuantity;
    private Direction direction;
    private String accountNumber;
    private Request request;

    /**
     * CTOR
     * New Order Event is created and is stored in
     * InMemoryEventStore and published on EventBus for subscription from
     * interested Listeners.
     *
     * @param id
     * @param securityId
     * @param quantity
     * @param price
     * @param directionString
     * @param accountNumber
     * @param request
     */
    public Order(long id, String securityId, BigDecimal quantity,
                 BigDecimal price, String directionString, String accountNumber,
                 Request request) {
        super(id);
        if(!directionString.isEmpty()) {
            if(directionString.equalsIgnoreCase("buy")) {
                this.direction = Direction.BUY;
            } else if(directionString.equalsIgnoreCase("sell")) {
                this.direction = Direction.SELL;
            } else {
                throw new RuntimeException("Unable to resolve Order " +
                        "direction, aceepted values are BUY or SELL");
            }
        } else {
            throw new RuntimeException("Order direction cannot be empty, " +
                    "accepted values are BUY or SELL");
        }
        OrderCreateEvent orderCreateEvent = new OrderCreateEvent(id,
                now(UTC), getNextVersion(), securityId, quantity, price,
                this.direction, accountNumber, request);
        applyNewEvent(orderCreateEvent);
    }

    /**
     * CTOR
     *
     * @param id
     * @param eventStream
     */
    public Order(long id, List<Event> eventStream) {
        super(id, eventStream);
    }

    /**
     * Order amendment / modify is handled by this function, where leaves
     * quantity of the order is modified and Order Amend Event is created.
     * This is stored in EventStore and published on to EventBus.
     *
     * @param amendQuantity
     */
    public void amendOrder(BigDecimal amendQuantity) {
        BigDecimal newQuantity = getLeavesQuantity().add(amendQuantity);
        OrderAmendEvent orderAmendEvent = new OrderAmendEvent(getId(),
                now(UTC), getNextVersion(), getSecurityId(), getQuantity(),
                newQuantity, getPrice(), getDirection(), getAccountNumber(),
                Request.AMEND);
        applyNewEvent(orderAmendEvent);
    }

    /**
     * Order Cancel event is created and published.
     */
    public void cancelOrder() {
        OrderCancelEvent orderCancelEvent = new OrderCancelEvent(getId(),
                now(UTC), getNextVersion(), getSecurityId(), getQuantity(),
                getPrice(), getDirection(), getAccountNumber(), Request.CANCEL);
        applyNewEvent(orderCancelEvent);
    }

    @SuppressWarnings("unused")
    private void apply(OrderCreateEvent event) {
        securityId = event.getSecurityId();
        quantity = event.getQuantity();
        leavesQuantity = event.getLeavesQuantity();
        price = event.getPrice();
        direction = event.getDirection();
        accountNumber = event.getAccountNumber();
        request = event.getRequest();

    }

    @SuppressWarnings("unused")
    private void apply(OrderAmendEvent event) {
        securityId = event.getSecurityId();
        quantity = event.getQuantity();
        leavesQuantity = event.getLeavesQuantity();
        price = event.getPrice();
        direction = event.getDirection();
        accountNumber = event.getAccountNumber();
        request = event.getRequest();
    }

    @SuppressWarnings("unused")
    private void apply(OrderCancelEvent event) {
        securityId = event.getSecurityId();
        quantity = event.getQuantity();
        leavesQuantity = event.getLeavesQuantity();
        price = event.getPrice();
        direction = event.getDirection();
        accountNumber = event.getAccountNumber();
        request = event.getRequest();
    }

    /**
     * Order direction.
     */
    public enum Direction {
        BUY, SELL
    }

    /**
     * Order request type.
     */
    public enum Request {
        NEW, AMEND, CANCEL
    }

    public String getSecurityId() {
        return securityId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getLeavesQuantity() { return leavesQuantity; }

    public BigDecimal getPrice() {
        return price;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Request getRequest() {
        return request;
    }
}
