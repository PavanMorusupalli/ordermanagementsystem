package com.mdaqtest.ordermanagementsystem.port.outgoing.adaptor.eventstore;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.EventStore;
import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class InMemoryEventStoreTest {

    private EventStore eventStore = new InMemoryEventStore();

    @Test
    void storeEventsInOrder() {
        long orderId = 12345L;
        Event e1 = new Event(orderId, now(UTC), 1){};
        Event e2 = new Event(orderId, now(UTC), 2){};
        Event e3 = new Event(orderId, now(UTC), 3){};
        eventStore.store(orderId, newArrayList(e1), 0);
        eventStore.store(orderId, newArrayList(e2), 1);
        eventStore.store(orderId, newArrayList(e3), 2);

        List<Event> eventStream = eventStore.load(orderId);
        assertThat(eventStream.size(), equalTo(3));
        assertThat(eventStream.get(0), equalTo(e1));
        assertThat(eventStream.get(1), equalTo(e2));
        assertThat(eventStream.get(2), equalTo(e3));
    }

    @Test
    void optimisticLocking() {
        long orderId = 99999L;
        Event e1 = new Event(orderId, now(UTC), 1){};
        Event e2 = new Event(orderId, now(UTC), 2){};
        Event e3 = new Event(orderId, now(UTC), 3){};
        eventStore.store(orderId, newArrayList(e1), 0);
        eventStore.store(orderId, newArrayList(e2), 1);
        assertThrows(
                OptimisticLockingException.class,
                () -> eventStore.store(orderId, newArrayList(e3), 1)
        );
    }

    @Test
    void loadedEventStreamIsImmutable() {
        long orderId = 56789L;
        Event e1 = new Event(orderId, now(UTC), 1){};
        eventStore.store(orderId, newArrayList(e1), 0);
        assertThrows(
                UnsupportedOperationException.class,
                () -> eventStore.load(orderId).add(mock(Event.class))
        );
    }

}
