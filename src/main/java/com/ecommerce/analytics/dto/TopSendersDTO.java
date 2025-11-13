package com.ecommerce.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSendersDTO {
    private Integer customerId;
    private String email;
    private String fullName;
    private BigDecimal totalSpend;
}
