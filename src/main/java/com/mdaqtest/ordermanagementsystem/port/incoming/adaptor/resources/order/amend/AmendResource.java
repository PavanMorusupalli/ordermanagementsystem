package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.amend;

import com.mdaqtest.ordermanagementsystem.domain.OptimisticLockingException;
import com.mdaqtest.ordermanagementsystem.service.order.OrderAmendCommand;
import com.mdaqtest.ordermanagementsystem.service.order.OrderNotFoundException;
import com.mdaqtest.ordermanagementsystem.service.order.OrderService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Command to send amend/modify quantity request to the OMS.
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/order_amend/{id}")
public class AmendResource {
    private final OrderService orderService;

    public AmendResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    public Response post(@PathParam("id") long orderId,
                         @Valid AmendDto amendDto)
            throws OrderNotFoundException, OptimisticLockingException {
        OrderAmendCommand command = new OrderAmendCommand(orderId,
                amendDto.getAmendQuantity());
        orderService.process(command);
        return Response.noContent().build();
    }
}
