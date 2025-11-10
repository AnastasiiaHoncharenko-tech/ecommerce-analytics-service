package com.github.honcharenko.ecommerceanalyticsservice.DTO;

import java.math.BigDecimal;

public class TopSendersDTO {

    private Integer customerId;
    private String email;
    private String fullName;
    private BigDecimal totalSpend;

    public TopSendersDTO() {
    }

    public TopSendersDTO(Integer customerId, String email, String fullName, BigDecimal totalSpend) {
        this.customerId = customerId;
        this.email = email;
        this.fullName = fullName;
        this.totalSpend = totalSpend;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(BigDecimal totalSpend) {
        this.totalSpend = totalSpend;
    }
}
