package com.mdaqtest.ordermanagementsystem.domain.model.order;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderAmendEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCancelEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCreateEvent;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

class OrderTest {

    @Test
    void newOrderHasNewOrderCreateEvent() {
        long orderId = 12345L;
        Order order = new Order(orderId, "ITG", BigDecimal.valueOf(500),
                BigDecimal.valueOf(700), "BUY", "ABC", Order.Request.NEW);
        List<Event> newEvents =order.getNewEvents();

        assertThat(newEvents.size(), equalTo(1));
        assertThat(newEvents.get(0), instanceOf(OrderCreateEvent.class));
        OrderCreateEvent event = (OrderCreateEvent) newEvents.get(0);
        assertThat(event.getOrderId(), equalTo(orderId));
        assertThat(event.getSecurityId(), equalTo("ITG"));
        assertThat(event.getQuantity(), equalTo(BigDecimal.valueOf(500)));
        assertThat(event.getLeavesQuantity(), equalTo(BigDecimal.valueOf(500)));
    }

    @Test
    void amendHasOrderAmendEvent() {
        long orderId = 9999L;

        Order order = new Order(orderId, "BMW", BigDecimal.valueOf(500),
                BigDecimal.valueOf(700), "BUY", "GER", Order.Request.NEW);

        order.amendOrder(BigDecimal.valueOf(-100));

        List<Event> newEvents = order.getNewEvents();

        assertThat(newEvents.size(), equalTo(2));
        assertThat(newEvents.get(1), instanceOf(OrderAmendEvent.class));
        OrderAmendEvent event = (OrderAmendEvent) newEvents.get(1);
        assertThat(event.getLeavesQuantity(), equalTo(BigDecimal.valueOf(400)));
    }

    @Test
    void cancelHasOrderCancelEvent() {
        long orderId = 334455L;

        Order order = new Order(orderId, "AAPL", BigDecimal.valueOf(500),
                BigDecimal.valueOf(700), "BUY", "USL", Order.Request.NEW);

        order.cancelOrder();

        List<Event> newEvents = order.getNewEvents();

        assertThat(newEvents.size(), equalTo(2));
        assertThat(newEvents.get(1), instanceOf(OrderCancelEvent.class));
        OrderCancelEvent event = (OrderCancelEvent) newEvents.get(1);
        assertThat(event.getLeavesQuantity(), equalTo(BigDecimal.ZERO));
    }
}

