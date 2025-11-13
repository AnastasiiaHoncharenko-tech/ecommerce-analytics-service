package com.ecommerce.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankByCategoryDTO {
    private Integer productId;
    private String productName;
    private String category;
    private BigDecimal totalQuantitySold;
    private Integer categoryRank;
}
