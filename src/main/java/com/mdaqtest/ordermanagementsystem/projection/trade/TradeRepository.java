package com.mdaqtest.ordermanagementsystem.projection.trade;

import java.util.List;

public interface TradeRepository {
    void save(TradeProjection tradeProjection);
    List<TradeProjection> getTrades(long orderId);
}
