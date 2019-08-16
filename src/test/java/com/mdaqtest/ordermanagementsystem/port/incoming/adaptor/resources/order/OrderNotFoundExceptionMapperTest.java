package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order;

import com.mdaqtest.ordermanagementsystem.service.order.OrderNotFoundException;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class OrderNotFoundExceptionMapperTest {

    static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addProvider(OrderNotFoundExceptionMapper.class)
            .addResource(new NeverFoundAccountResource())
            .build();

    @Test
    void returnNotFound() {
        Response response = RESOURCES.client()
                .target(format("/never-found-order-resource/%s", 12345L))
                .request().get();
        response.close();
        assertThat(response.getStatus(), equalTo(404));
        assertThat(NeverFoundAccountResource.calls, equalTo(0));
    }

    @Produces(APPLICATION_JSON)
    @Path("/never-found-account-resource/{id}")
    public static class NeverFoundAccountResource {
        static int calls = 0;

        @GET
        public Response get(@PathParam("id") String id) throws OrderNotFoundException {
            calls++;
            throw new OrderNotFoundException(Long.valueOf(id));
        }
    }
}
