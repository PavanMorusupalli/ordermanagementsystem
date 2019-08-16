package com.mdaqtest.ordermanagementsystem.service.order;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.EventStore;
import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;
import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderAmendEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCancelEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCreateEvent;
import com.mdaqtest.ordermanagementsystem.port.outgoing.adaptor.eventstore.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private EventStore eventStore;
    private EventBusCounter eventBusCounter;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        eventStore = spy(new InMemoryEventStore());
        EventBus eventBus = new EventBus();
        eventBusCounter = new EventBusCounter();
        eventBus.register(eventBusCounter);
        orderService = new OrderService(eventStore, eventBus);
    }

    @Test
    void retryOnOrderCancelConflictsUpToThreeAttempts() {
        Order order =
                orderService.process(new OrderNewCommand(12345L, "ITG",
                        BigDecimal.valueOf(500), BigDecimal.valueOf(500), "BUY",
                        "ITG"));
        long id = order.getId();
        orderService.process(new OrderAmendCommand(id,
                BigDecimal.valueOf(-500)));
        doThrow(OptimisticLockingException.class)
                .doThrow(OptimisticLockingException.class)
                .doCallRealMethod()
                .when(eventStore).store(eq(id), anyListOf(Event.class), anyInt());

        orderService.process(new OrderCancelCommand(id));
        int creationAttempts = 1, AmendAttempts = 1, CancelAttempts = 3;
        int loadTimes = AmendAttempts + CancelAttempts + 2;
        int storeTimes = creationAttempts + AmendAttempts + CancelAttempts;
        verify(eventStore, times(loadTimes)).load(eq(id));
        verify(eventStore, times(storeTimes)).store(eq(id), anyListOf(Event.class), anyInt());
        assertThat(eventBusCounter.eventsCount.get(OrderCreateEvent.class),
                equalTo(1));
        assertThat(eventBusCounter.eventsCount.get(OrderAmendEvent.class), equalTo(1));
        assertThat(eventBusCounter.eventsCount.get(OrderCancelEvent.class), equalTo(1));
        assertThat(eventBusCounter.eventsCount.size(), equalTo(3));
    }

    private static class EventBusCounter {
        Map<Class<?>, Integer> eventsCount = new ConcurrentHashMap<>();

        @Subscribe
        @SuppressWarnings("unused")
        public void handle(Object event) {
            eventsCount.merge(event.getClass(), 1, (oldValue, value) -> oldValue + value);
        }
    }
}
