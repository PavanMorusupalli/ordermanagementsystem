package com.mdaqtest.ordermanagementsystem.bootstrap;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.mdaqtest.ordermanagementsystem.domain.EventStore;
import com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.*;
import com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.amend.AmendResource;
import com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.cancel.CancelResource;
import com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.lock.OptimisticLockingExceptionMapper;
import com.mdaqtest.ordermanagementsystem.port.outgoing.adaptor.eventstore.InMemoryEventStore;
import com.mdaqtest.ordermanagementsystem.projection.trade.*;
import com.mdaqtest.ordermanagementsystem.service.order.OrderService;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

/**
 * Order Management System launcher class.
 *
 * @author Pavan Morusupalli
 */
public class OrderManagementApplication extends Application<Configuration>
{

    public static void main(String... args) throws Exception {
        new OrderManagementApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        registerFilters(environment);
        registerExceptionMappers(environment);
        registerHypermediaSupport(environment);
        registerResources(environment);
    }

    private void registerFilters(Environment environment) {
        environment.jersey().register(new LoggingFeature(
                getLogger(DEFAULT_LOGGER_NAME), INFO, PAYLOAD_ANY, 1024));
    }

    private void registerExceptionMappers(Environment environment) {
        environment.jersey().register(OrderNotFoundExceptionMapper.class);
        environment.jersey().register(OptimisticLockingExceptionMapper.class);
    }

    private void registerHypermediaSupport(Environment environment) {
        environment.jersey().getResourceConfig().register(DeclarativeLinkingFeature.class);
    }

    private void registerResources(Environment environment) {
        // Event Bus and store initialization.
        EventStore eventStore = new InMemoryEventStore();
        EventBus eventBus = new AsyncEventBus(newSingleThreadExecutor());
        // Service registration.
        OrderService orderService = new OrderService(eventStore, eventBus);
        // domain model.
        environment.jersey().register(new OrderResource(orderService));
        environment.jersey().register(new OrderMergedResource(orderService));
        environment.jersey().register(new OrderStateResource(orderService));
        environment.jersey().register(new OrderStatesResource(orderService));
        environment.jersey().register(new AmendResource(orderService));
        environment.jersey().register(new CancelResource(orderService));
        // read model.
        TradeRepository tradeRepository = new InMemoryTradeRepository();
        eventBus.register(new TradeListener(tradeRepository));
        environment.jersey().register(new TradeStateResource(tradeRepository));
        environment.jersey().register(new TradeStatesResource(tradeRepository));
    }
}