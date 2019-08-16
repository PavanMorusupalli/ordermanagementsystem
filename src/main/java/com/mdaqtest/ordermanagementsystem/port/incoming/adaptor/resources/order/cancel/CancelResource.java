package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.cancel;

import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;
import com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.amend.AmendDto;
import com.mdaqtest.ordermanagementsystem.service.order.OrderCancelCommand;
import com.mdaqtest.ordermanagementsystem.service.order.OrderNotFoundException;
import com.mdaqtest.ordermanagementsystem.service.order.OrderService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Command to send cancel order request.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/order_cancel/{id}")
public class CancelResource {

    private final OrderService orderService;

    public CancelResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    public Response post(@PathParam("id") long orderId)
            throws OrderNotFoundException, OptimisticLockingException {
        OrderCancelCommand command = new OrderCancelCommand(orderId);
        orderService.process(command);
        return Response.noContent().build();
    }
}
