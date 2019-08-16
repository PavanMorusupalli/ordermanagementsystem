package com.mdaqtest.ordermanagementsystem.projection.trade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Class which stores Trade events in momory by OrderId sorted by version.
 */
public class InMemoryTradeRepository implements TradeRepository {

    private Map<Long, List<TradeProjection>> orderTransactions =
            new ConcurrentHashMap<>();
    @Override
    public void save(TradeProjection tradeProjection) {
        orderTransactions.merge(
                tradeProjection.getOrderId(),
                newArrayList(tradeProjection),
                (oldValue, value) ->
                        Stream.concat(oldValue.stream(), value.stream()).collect(toList()));
    }

    @Override
    public List<TradeProjection> getTrades(long orderId) {
        return orderTransactions.getOrDefault(orderId, emptyList()).stream()
                .sorted(comparing(TradeProjection::getVersion))
                .collect(toList());
    }
}
