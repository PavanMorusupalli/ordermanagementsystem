package com.mdaqtest.ordermanagementsystem.domain;

import java.util.List;

/**
 * Event Store interface.
 */
public interface EventStore {

    void store(long aggregateId, List<Event> newEvents, int baseVersion)
            throws OptimisticLockingException;

    List<Event> load(long aggregateId);
}

