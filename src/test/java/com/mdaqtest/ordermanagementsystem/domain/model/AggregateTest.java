package com.mdaqtest.ordermanagementsystem.domain.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mdaqtest.ordermanagementsystem.domain.Aggregate;
import com.mdaqtest.ordermanagementsystem.domain.Event;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.List;

class AggregateTest {

    @Test
    void newAggregateHasBaseVersion0() {
        Aggregate aggregate = new Aggregate(12345L){};
        assertThat(aggregate.getBaseVersion(), equalTo(0));
    }

    @Test
    void newEventsListIsImmutable() {
        long id = 909L;
        Aggregate aggregate = new Aggregate(id){};
        assertThrows(
                UnsupportedOperationException.class,
                () -> aggregate.getNewEvents().add(new Event(id, now(UTC), 1){})
        );
    }

    @Test
    void replayEventStreamUsingChildClassMethods() {
        long id = 9999L;
        DummyEvent eventWithCorrespondingHandler = new DummyEvent(id, now(UTC), 1);
        List<Event> eventStream = singletonList(eventWithCorrespondingHandler);
        new BackCallerAggregate(id, eventStream);
        assertThat(eventWithCorrespondingHandler.getCalledBackTimes(), equalTo(1));
    }

    @Test
    void failReplayOfEventWithoutProperChildClassMethodHandler() {
        long id = 22334L;
        Event eventWithoutCorrespondingHandler = new Event(id, now(UTC), 1){};
        List<Event> eventStream = singletonList(eventWithoutCorrespondingHandler);
        assertThrows(
                UnsupportedOperationException.class,
                () -> new Aggregate(id, eventStream){}
        );
    }

    @Test
    void propagatesExceptionOfFailingReplay() {
        long id = 7865L;
        ArithmeticException replayException = new ArithmeticException();
        ProblematicEvent problematicEvent = new ProblematicEvent(id, now(UTC), 1, replayException);
        List<Event> eventStream = singletonList(problematicEvent);
        assertThrows(
                ArithmeticException.class,
                () -> new BackCallerAggregate(id, eventStream)
        );
    }

    @Test
    void replayedAggregateKeepsEventStreamVersionAsItsBaseVersion() {
        long id = 23689L;
        List<Event> eventStream = singletonList(new DummyEvent(id, now(UTC), 1));
        Aggregate aggregate = new BackCallerAggregate(id, eventStream);
        assertThat(aggregate.getBaseVersion(), equalTo(1));
        aggregate.applyNewEvent(new DummyEvent(id, now(UTC), 2));
        assertThat(aggregate.getBaseVersion(), equalTo(1));
    }

    @Test
    void nextVersionOfEmptyEventStreamIs1() {
        Aggregate aggregate = new Aggregate(56789L){};
        assertThat(aggregate.getNextVersion(), equalTo(1));
        assertThat(aggregate.getNextVersion(), equalTo(1));
    }

    @Test
    void nextVersionOfExistingEventStreamIsTotalOfEventsPlus1() {
        long id = 11221L;
        List<Event> eventStream = singletonList(new DummyEvent(id, now(UTC), 1));
        Aggregate aggregate = new BackCallerAggregate(id, eventStream);
        assertThat(aggregate.getNextVersion(), equalTo(2));

        aggregate.applyNewEvent(new DummyEvent(id, now(UTC), 2));
        assertThat(aggregate.getNextVersion(), equalTo(3));
    }

    @Test
    void failOnWrongNewEventVersion() {
        long id = 776655L;
        List<Event> eventStream = singletonList(new DummyEvent(id, now(UTC), 1));
        Aggregate aggregate = new BackCallerAggregate(id, eventStream);
        assertThrows(
                IllegalArgumentException.class,
                () -> aggregate.applyNewEvent(new DummyEvent(id, now(UTC), 1))
        );
    }

    private static class BackCallerAggregate extends Aggregate {
        private BackCallerAggregate(long id, List<Event> eventStream) {
            super(id, eventStream);
        }

        @SuppressWarnings("unused")
        private void apply(DummyEvent e) {
            e.callback();
        }

        @SuppressWarnings("unused")
        private void apply(ProblematicEvent e) {
            e.callback();
        }
    }

    private static class DummyEvent extends Event {
        private int calledBackTimes = 0;

        private DummyEvent(long aggregateId, DateTime timestamp, int version) {
            super(aggregateId, timestamp, version);
        }

        void callback() {
            calledBackTimes++;
        }

        int getCalledBackTimes() {
            return calledBackTimes;
        }
    }

    private static class ProblematicEvent extends Event {
        private RuntimeException exception;

        private ProblematicEvent(long aggregateId, DateTime timestamp,
                                 int version, RuntimeException exception) {
            super(aggregateId, timestamp, version);
            this.exception = exception;
        }

        void callback() {
            throw exception;
        }
    }
}
