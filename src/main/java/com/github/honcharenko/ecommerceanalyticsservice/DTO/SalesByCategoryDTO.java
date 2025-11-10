package com.github.honcharenko.ecommerceanalyticsservice.DTO;

import java.math.BigDecimal;

public class SalesByCategoryDTO {
    private String category;
    private BigDecimal totalSales;

    public SalesByCategoryDTO() {
    }

    public SalesByCategoryDTO(String category, BigDecimal totalSales) {
        this.category = category;
        this.totalSales = totalSales;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
}
