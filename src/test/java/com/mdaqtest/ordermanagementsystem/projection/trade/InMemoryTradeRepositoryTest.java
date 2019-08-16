package com.mdaqtest.ordermanagementsystem.projection.trade;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;

class InMemoryTradeRepositoryTest {

    private TradeRepository tradeRepository = new InMemoryTradeRepository();

    @Test
    void listEventsSortedByVersion() {
        long orderId = 12345L;
        TradeProjection tp2 = new TradeProjection(orderId, "ITG",
                BigDecimal.valueOf(500), BigDecimal.valueOf(500),
                BigDecimal.valueOf(100), "BUY", "ABC", 99999l,
                BigDecimal.ZERO, BigDecimal.ZERO,
                TradeProjection.TradeState.NEW, now(UTC), 2);

        TradeProjection tp1 = new TradeProjection(orderId, "ITG",
                BigDecimal.valueOf(500), BigDecimal.valueOf(500),
                BigDecimal.valueOf(100), "BUY", "ABC", 99999l,
                BigDecimal.ZERO, BigDecimal.ZERO,
                TradeProjection.TradeState.NEW, now(UTC), 1);
        tradeRepository.save(tp2);
        tradeRepository.save(tp1);

        List<TradeProjection> trades = tradeRepository.getTrades(orderId);
        assertThat(trades.get(0), equalTo(tp1));
        assertThat(trades.get(1), equalTo(tp2));
    }
}
