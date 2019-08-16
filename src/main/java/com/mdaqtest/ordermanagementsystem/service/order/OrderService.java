package com.mdaqtest.ordermanagementsystem.service.order;

import com.google.common.eventbus.EventBus;
import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.EventStore;
import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;
import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderAmendEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCancelEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCreateEvent;
import com.mdaqtest.ordermanagementsystem.service.Retrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

/**
 * Order Service class.
 */
public class OrderService {

    private final EventStore eventStore;
    private final EventBus eventBus;
    private final Retrier conflictRetrier;

    public OrderService(EventStore eventStore, EventBus eventBus) {
        this.eventStore = checkNotNull(eventStore);
        this.eventBus = checkNotNull(eventBus);
        int maxAttempts = 3;
        this.conflictRetrier = new Retrier(singletonList(OptimisticLockingException.class), maxAttempts);
    }

    public Optional<Order> loadOrder(long id) {
        List<Event> eventStream = eventStore.load(id);
        if (eventStream.isEmpty()) return Optional.empty();
        return Optional.of(new Order(id, eventStream));
    }

    public List<Event> loadOrderList(long id) {
        List<Event> eventStream = eventStore.load(id);
        if (eventStream.isEmpty()) return new ArrayList<>();
        return eventStream;
    }

    public Event getLatestEventFromList(long orderId) {
        List<Event> eventList = loadOrderList(orderId);
        if (eventList.isEmpty()) {
            return null;
        }
        return eventList.get(eventList.size() - 1);
    }

    public Order process(OrderNewCommand command) {
        Order order = new Order(command.getOrderId(), command.getSecurityId(),
                command.getQuantity(), command.getPrice(),
                command.getDirection(), command.getAccountNumber(),
                Order.Request.NEW);
        storeAndPublishEvents(order);
        return order;
    }

    public Order process(OrderAmendCommand command)
            throws OrderNotFoundException, OptimisticLockingException {
        long orderId = command.getOrderId();
        return conflictRetrier.get(() -> {
            Event e = getLatestEventFromList(orderId);
            if (e.getType().equalsIgnoreCase("ORDER_CREATE")) {
                if (((OrderCreateEvent) e).getOrderState() ==
                        OrderCreateEvent.OrderCreateSubType.NEW_REJECTED) {
                    return null;
                }
            } else if (e.getType().equalsIgnoreCase("ORDER_AMEND")) {
                if (((OrderAmendEvent) e).getOrderState() ==
                        OrderAmendEvent.OrderAmendSubType.AMEND_REJECTED) {
                    return null;
                }
            } else if (e.getType().equalsIgnoreCase("ORDER_CANCEL")) {
                // Cannot amend on a cancelled request.
                return null;
            }
            return process(orderId,
                    order -> order.amendOrder(command.getAmount()));
        });
    }

    public Order process(OrderCancelCommand command) {
        long orderId = command.getOrderId();
        return conflictRetrier.get(() -> {
            Event e = getLatestEventFromList(orderId);
            if (e.getType().equalsIgnoreCase("ORDER_CREATE")) {
                if (((OrderCreateEvent) e).getOrderState() ==
                        OrderCreateEvent.OrderCreateSubType.NEW_REJECTED) {
                    return null;
                }
            } else if (e.getType().equalsIgnoreCase("ORDER_CANCEL")) {
                // Cannot cancel on a cancelled request.
                return null;
            }
            return process(command.getOrderId(), order -> order.cancelOrder());
        });
    }

    private Order process(long orderId, Consumer<Order> consumer)
            throws OptimisticLockingException {
        return conflictRetrier.get(() -> {
            Optional<Order> possibleOrder = loadOrder(orderId);
            Order order =
                    possibleOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
            consumer.accept(order);
            storeAndPublishEvents(order);
            return order;
        });
    }

    private void storeAndPublishEvents(Order order) throws OptimisticLockingException {
        eventStore.store(order.getId(), order.getNewEvents(),
                order.getBaseVersion());
        order.getNewEvents().forEach(eventBus::post);
    }
}