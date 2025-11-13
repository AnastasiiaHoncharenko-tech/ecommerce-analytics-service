package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.*;
import com.ecommerce.analytics.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<SalesByCategoryDTO> getSalesByCategory() {
        return analyticsRepository
                .getSalesByCategory()
                .stream()
                .map(record -> {
                    String category = record.value1();
                    BigDecimal totalSales = record.value2();
                    return new SalesByCategoryDTO(category, totalSales);
                })
                .collect(Collectors.toList());
    }

    public List<TopSellingProductsDTO> getTopSellingProducts(int limit) {
        return analyticsRepository
                .getTopSellingProducts(limit)
                .stream()
                .map(record -> {
                    Integer productId = record.value1();
                    String productName = record.value2();
                    Long totalQuantitySold = record.value3().longValue();
                    return new TopSellingProductsDTO(productId, productName, totalQuantitySold);
                })
                .collect(Collectors.toList());
    }

    public List<TopSendersDTO> getTopSenders(int limit) {
        return analyticsRepository
                .getTopSpenders(limit)
                .stream()
                .map(record -> {
                    Integer customerId = record.value1();
                    String email = record.value2();
                    String firstName = record.value3();
                    String lastName = record.value4();
                    BigDecimal totalSpend = record.value5();
                    String fullName = firstName + " " + lastName;
                    return new TopSendersDTO(customerId, email, fullName, totalSpend);
                })
                .collect(Collectors.toList());
    }

    public List<StatusSummaryDTO> getOrderCountByStatusName() {
        return analyticsRepository
                .getOrderCountByStatusName()
                .stream()
                .map(record -> {
                    String statusName = record.value1();
                    Long orderCount = record.value2();
                    return new StatusSummaryDTO(statusName, orderCount);
                })
                .collect(Collectors.toList());
    }

    public AverageOrderValueDTO getAverageOrderValue() {
        BigDecimal averageOrderValue = analyticsRepository
                .getAverageOrderValue()
                .value1();
        return new AverageOrderValueDTO(averageOrderValue);
    }

}
