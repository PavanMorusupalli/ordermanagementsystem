package com.mdaqtest.ordermanagementsystem.port.outgoing.adaptor.eventstore;

import com.google.common.collect.ImmutableList;
import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.EventStore;
import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Class which stores event state per OrderIs in-memory.
 */
public class InMemoryEventStore implements EventStore
{
    private final Map<Long, List<Event>> eventStore = new ConcurrentHashMap<>();

    @Override
    public void store(long aggregateId, List<Event> newEvents, int baseVersion)
            throws OptimisticLockingException {
        eventStore.merge(aggregateId, newEvents, (oldValue, value) -> {
            if (baseVersion != oldValue.get(oldValue.size() - 1).getVersion())
                throw new OptimisticLockingException("Base version does not match current stored version");

            return Stream.concat(oldValue.stream(), value.stream()).collect(toList());
        });
    }

    @Override
    public List<Event> load(long aggregateId) {
        return ImmutableList.copyOf(eventStore.getOrDefault(aggregateId, emptyList()));
    }
}
