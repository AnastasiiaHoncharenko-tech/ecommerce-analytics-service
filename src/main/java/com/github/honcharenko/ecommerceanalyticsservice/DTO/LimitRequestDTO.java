package com.github.honcharenko.ecommerceanalyticsservice.DTO;

public record LimitRequestDTO(Integer limit) {
    public LimitRequestDTO {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100;
        }
    }

    public Integer getLimit() {
        return limit;
    }
}
