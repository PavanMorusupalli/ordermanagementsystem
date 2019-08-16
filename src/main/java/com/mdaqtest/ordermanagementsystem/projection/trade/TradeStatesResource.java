package com.mdaqtest.ordermanagementsystem.projection.trade;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Gets all the trade events for a given OrderId in OMS.
 */
@Produces(APPLICATION_JSON)
@Path("/trade_states/{id}")
public class TradeStatesResource {
    private TradeRepository tradeRepository;

    public TradeStatesResource(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @GET
    public Response get(@PathParam("id") long orderId) {
        List<TradeProjection> tradeProjections =
                tradeRepository.getTrades(orderId);
        if(tradeProjections.isEmpty()) return Response.status(NOT_FOUND).build();
        return Response.ok(tradeProjections).build();
    }
}
