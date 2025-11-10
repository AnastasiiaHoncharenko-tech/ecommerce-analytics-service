package com.github.honcharenko.ecommerceanalyticsservice.controller;

import com.github.honcharenko.ecommerceanalyticsservice.DTO.*;
import com.github.honcharenko.ecommerceanalyticsservice.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/salesByCategory")
    public List<SalesByCategoryDTO> getSalesByCategory(){
        return analyticsService.getSalesByCategory();
    }

    @GetMapping("/topSellingProducts")
    public List<TopSellingProductsDTO> getTopSellingProducts(){
        return analyticsService.getTopSellingProducts();
    }

    @GetMapping("/topSpenders")
    public List<TopSendersDTO> getTopSpenders(){
        return analyticsService.getTopSenders();
    }

    @GetMapping("/statusSummary")
    public List<StatusSummaryDTO> getStatusSummary(){
        return analyticsService.getStatusSummary();
    }

    @GetMapping("/averageOrderValue")
    public AverageOrderValueDTO getAverageOrderValue(){
        return analyticsService.getAverageOrderValue();
    }
}
