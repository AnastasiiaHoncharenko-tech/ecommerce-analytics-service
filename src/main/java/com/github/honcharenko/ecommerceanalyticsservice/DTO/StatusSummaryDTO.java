package com.github.honcharenko.ecommerceanalyticsservice.DTO;

public class StatusSummaryDTO {

    private String statusName;
    private Long orderCount;

    public StatusSummaryDTO() {
    }

    public StatusSummaryDTO(String statusName, Long orderCount) {
        this.statusName = statusName;
        this.orderCount = orderCount;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }
}
