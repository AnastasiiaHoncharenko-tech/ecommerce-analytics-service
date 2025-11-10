package com.github.honcharenko.ecommerceanalyticsservice.DTO;

import java.math.BigDecimal;

public class AverageOrderValueDTO {

    private BigDecimal averageOrderValue;

    public AverageOrderValueDTO() {
    }

    public AverageOrderValueDTO(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}
