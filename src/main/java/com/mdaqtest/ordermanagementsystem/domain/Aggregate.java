package com.mdaqtest.ordermanagementsystem.domain;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

/**
 * Abstract class where event stream aggregation is handled.
 */
public abstract class Aggregate
{
    private long id;
    private int baseVersion;
    private List<Event> newEvents;

    /**
     * CTOR
     * @param id
     */
    protected Aggregate(long id) {
        this(id, emptyList());
    }

    /**
     * CTOR
     * @param id
     * @param eventStream
     */
    protected Aggregate(long id, List<Event> eventStream) {
        checkNotNull(id);
        checkNotNull(eventStream);
        this.id = id;
        eventStream.forEach(e -> {
            apply(e);
            this.baseVersion = e.getVersion();
        });
        this.newEvents = new ArrayList<>();
    }

    /**
     * This method handles the invocation of Event sub-class's apply method,
     * wrapping the specific event and add it to Event list.
     * @param event
     */
    public void applyNewEvent(Event event) {
        checkArgument(event.getVersion() == getNextVersion(),
                "New event version '%d' does not match expected next version '%d'",
                event.getVersion(), getNextVersion());
        apply(event);
        newEvents.add(event);
    }

    private void apply(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (InvocationTargetException e) {
            Throwables.propagate(e.getCause());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new UnsupportedOperationException(
                    format("Aggregate '%s' doesn't apply event type '%s'", this.getClass(), event.getClass()), e);
        }
    }

    /**
     * Get orderId for order.
     */
    public long getId() {
        return id;
    }

    /**
     * Get base version number.
     */
    public int getBaseVersion() {
        return baseVersion;
    }

    /**
     * Get the immutable copy of event list.
     */
    public List<Event> getNewEvents() {
        return ImmutableList.copyOf(newEvents);
    }

    /**
     * Get the next version of event.
     * Used for tracking the number of events and sequence.
     */
    public int getNextVersion() {
        return baseVersion + newEvents.size() + 1;
    }
}
