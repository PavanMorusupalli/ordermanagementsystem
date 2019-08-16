package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order;

import com.mdaqtest.ordermanagementsystem.domain.Event;
import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import com.mdaqtest.ordermanagementsystem.service.order.OrderService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Get the latest order state.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/order_state/{id}")
public class OrderStateResource {

    private final OrderService orderService;

    public OrderStateResource(OrderService orderService) {
        this.orderService = checkNotNull(orderService);
    }

    @GET
    public Response get(@PathParam("id") long orderId) {
        Optional<Order> possibleOrder =
                orderService.loadOrder(orderId);
        if (!possibleOrder.isPresent()) return Response.status(NOT_FOUND).build();
        Event latestState = orderService.getLatestEventFromList(orderId);
        return Response.ok(latestState).build();
    }
}
