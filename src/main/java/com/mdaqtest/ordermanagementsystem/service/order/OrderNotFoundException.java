package com.mdaqtest.ordermanagementsystem.service.order;

import static java.lang.String.format;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(long id) {
        super(format("Order with id '%s' could not be found", id));
    }
}
