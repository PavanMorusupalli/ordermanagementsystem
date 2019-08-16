package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order;

import com.mdaqtest.ordermanagementsystem.domain.order.Order;
import com.mdaqtest.ordermanagementsystem.service.order.OrderNewCommand;
import com.mdaqtest.ordermanagementsystem.service.order.OrderService;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.UriBuilder.fromResource;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.net.URI;

/**
 * Command to create a new Order in OMS.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/order")
public class OrderResource {
    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = checkNotNull(orderService);
    }

    @POST
    public Response post(@Valid OrderDto orderDto) {
        OrderNewCommand command = new OrderNewCommand(orderDto.getOrderId(),
                orderDto.getSecurityId(), orderDto.getQuantity(),
                orderDto.getPrice(), orderDto.getDirection(),
                orderDto.getAccountNumber());
        Order order = orderService.process(command);
        URI orderUri = fromResource(OrderResource.class).build(order.getId());
        return Response.created(orderUri).build();
    }
}
