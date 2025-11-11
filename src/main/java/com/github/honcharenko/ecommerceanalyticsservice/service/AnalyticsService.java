package com.github.honcharenko.ecommerceanalyticsservice.service;

import com.github.honcharenko.ecommerceanalyticsservice.DTO.*;
import com.github.honcharenko.ecommerceanalyticsservice.repository.AnalyticsRepository;
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
                .map(
                        record -> new SalesByCategoryDTO(
                                record.value1(),
                                record.value2()
                        )
                ).collect(Collectors.toList());
    }

    public List<TopSellingProductsDTO> getTopSellingProducts(int limit) {

        return analyticsRepository
                .getTopSellingProducts(limit)
                .stream()
                .map(
                        record -> new TopSellingProductsDTO(
                                record.value1(),
                                record.value2(),
                                record.value3().longValue()
                        )
                ).collect(Collectors.toList());
    }

    public List<TopSendersDTO> getTopSenders(int limit) {
        return analyticsRepository
                .getTopSpenders(limit)
                .stream()
                .map(record -> new TopSendersDTO(
                        record.value1(),
                        record.value2(),
                        record.value3() + " " + record.value4(),
                        record.value5()
                )).collect(Collectors.toList());
    }

    public List<StatusSummaryDTO> getStatusSummary() {
        return analyticsRepository
                .getStatusSummary()
                .stream()
                .map(record -> new StatusSummaryDTO(
                        record.value1(),
                        record.value2()
                )).collect(Collectors.toList());
    }

    public AverageOrderValueDTO getAverageOrderValue() {
        return new AverageOrderValueDTO(analyticsRepository
                .getAverageOrderValue().value1());
    }

}
