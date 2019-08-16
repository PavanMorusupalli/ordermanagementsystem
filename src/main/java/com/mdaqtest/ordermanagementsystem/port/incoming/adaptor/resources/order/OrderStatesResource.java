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
 * Get all the states of the given order in OMS.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/order_states/{id}")
public class OrderStatesResource {

    private final OrderService orderService;

    public OrderStatesResource(OrderService orderService) {
        this.orderService = checkNotNull(orderService);
    }

    @GET
    public Response get(@PathParam("id") long orderId) {
        Optional<Order> possibleOrder =
                orderService.loadOrder(orderId);
        if (!possibleOrder.isPresent()) return Response.status(NOT_FOUND).build();
        List<Event> eventList = orderService.loadOrderList(orderId);
        return Response.ok(eventList).build();
    }
}
