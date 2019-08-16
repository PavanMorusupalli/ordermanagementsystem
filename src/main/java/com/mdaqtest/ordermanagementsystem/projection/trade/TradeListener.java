package com.mdaqtest.ordermanagementsystem.projection.trade;

import com.google.common.eventbus.Subscribe;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderAmendEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCancelEvent;
import com.mdaqtest.ordermanagementsystem.domain.order.OrderCreateEvent;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listen to Order events published on Event Bus and takes appropriate Trade
 * actions.
 */
public class TradeListener {

    private TradeRepository tradeRepository;

    public TradeListener(TradeRepository tradeRepository) {
        this.tradeRepository = checkNotNull(tradeRepository);
    }

    @Subscribe
    public void handle(OrderCreateEvent event) {
        if (event.getOrderState()
                == OrderCreateEvent.OrderCreateSubType.NEW_ACCEPTED) {
            TradeProjection tradeProjection =
                    new TradeProjection(event.getOrderId(),
                            event.getSecurityId(),event.getQuantity(),
                            event.getLeavesQuantity(), event.getPrice(),
                            event.getDirection().toString(),
                            event.getAccountNumber(),
                            event.getOrderId() + 1000L,
                            BigDecimal.ZERO, BigDecimal.ZERO,
                            TradeProjection.TradeState.NEW,
                            event.getTimestamp(), event.getVersion());
            tradeRepository.save(tradeProjection);
        } else {
            TradeProjection tradeProjection =
                    new TradeProjection(event.getOrderId(),
                            event.getSecurityId(),event.getQuantity(),
                            event.getLeavesQuantity(), event.getPrice(),
                            event.getDirection().toString(),
                            event.getAccountNumber(),
                            event.getOrderId() + 1000L,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            TradeProjection.TradeState.CANCEL,
                            event.getTimestamp(), event.getVersion());
            tradeRepository.save(tradeProjection);
        }
    }

    @Subscribe
    public void handle(OrderAmendEvent event) {
        if (event.getOrderState()
                == OrderAmendEvent.OrderAmendSubType.AMEND_ACCEPTED) {
            if (event.getLeavesQuantity().equals(BigDecimal.ZERO)) {
                TradeProjection tradeProjection =
                        new TradeProjection(event.getOrderId(),
                                event.getSecurityId(),
                                event.getQuantity(),
                                event.getLeavesQuantity(),
                                event.getPrice(),
                                event.getDirection().toString(),
                                event.getAccountNumber(),
                                event.getOrderId() + 1000L,
                                event.getQuantity(),
                                event.getPrice(),
                                TradeProjection.TradeState.COMPLETE,
                                event.getTimestamp(),
                                event.getVersion());
                tradeRepository.save(tradeProjection);
            } else {
                TradeProjection tradeProjection =
                        new TradeProjection(event.getOrderId(),
                                event.getSecurityId(),
                                event.getQuantity(),
                                event.getLeavesQuantity(),
                                event.getPrice(),
                                event.getDirection().toString(),
                                event.getAccountNumber(),
                                event.getOrderId() + 1000L,
                                event.getQuantity().subtract(event.getLeavesQuantity()),
                                event.getPrice(),
                                TradeProjection.TradeState.AMEND,
                                event.getTimestamp(),
                                event.getVersion());
                tradeRepository.save(tradeProjection);
            }
        } else {
            TradeProjection tradeProjection =
                    new TradeProjection(event.getOrderId(),
                            event.getSecurityId(),
                            event.getQuantity(),
                            event.getLeavesQuantity(),
                            event.getPrice(),
                            event.getDirection().toString(),
                            event.getAccountNumber(),
                            event.getOrderId() + 1000L,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            TradeProjection.TradeState.CANCEL,
                            event.getTimestamp(),
                            event.getVersion());
            tradeRepository.save(tradeProjection);
        }
    }

    @Subscribe
    public void handle(OrderCancelEvent event) {
        TradeProjection tradeProjection =
                new TradeProjection(event.getOrderId(),
                        event.getSecurityId(),
                        event.getQuantity(),
                        BigDecimal.ZERO,
                        event.getPrice(),
                        event.getDirection().toString(),
                        event.getAccountNumber(),
                        event.getOrderId() + 1000L,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        TradeProjection.TradeState.CANCEL,
                        event.getTimestamp(),
                        event.getVersion());
        tradeRepository.save(tradeProjection);
    }
}
