package com.github.honcharenko.ecommerceanalyticsservice.controller;

import com.github.honcharenko.ecommerceanalyticsservice.DTO.*;
import com.github.honcharenko.ecommerceanalyticsservice.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/topSellingProducts")
    public List<TopSellingProductsDTO> getTopSellingProducts(@RequestBody LimitRequestDTO request){
        return analyticsService.getTopSellingProducts(request.getLimit());
    }

    @PostMapping("/topSpenders")
    public List<TopSendersDTO> getTopSpenders(@RequestBody LimitRequestDTO request){
        return analyticsService.getTopSenders(request.getLimit());
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
