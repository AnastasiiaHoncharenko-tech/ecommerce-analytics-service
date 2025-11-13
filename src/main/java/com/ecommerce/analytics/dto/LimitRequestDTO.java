package com.ecommerce.analytics.dto;

import com.ecommerce.analytics.util.ValidationUtils;

public record LimitRequestDTO(Integer limit) {
    public LimitRequestDTO {
        limit = ValidationUtils.validateAndNormalizeLimit(limit);
    }

    public Integer getLimit() {
        return limit;
    }
}
