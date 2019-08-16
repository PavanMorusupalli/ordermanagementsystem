package com.mdaqtest.ordermanagementsystem.port.incoming.adaptor.resources.order.amend;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AmendDto {

    @NotNull
    private BigDecimal amendQuantity;

    public BigDecimal getAmendQuantity() {
        return amendQuantity;
    }

    public void setAmendQuantity(BigDecimal amendQuantity) {
        this.amendQuantity = amendQuantity;
    }
}
