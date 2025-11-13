package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.dto.*;
import com.ecommerce.analytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales-by-category")
    public List<SalesByCategoryDTO> getSalesByCategory(){
        return analyticsService.getSalesByCategory();
    }

    @PostMapping("/top-selling-products")
    public List<TopSellingProductsDTO> getTopSellingProducts(@RequestBody LimitRequestDTO request){
        return analyticsService.getTopSellingProducts(request.getLimit());
    }

    @PostMapping("/top-spenders")
    public List<TopSendersDTO> getTopSpenders(@RequestBody LimitRequestDTO request){
        return analyticsService.getTopSenders(request.getLimit());
    }

    @GetMapping("/status-summary")
    public List<StatusSummaryDTO> getOrderCountByStatusName(){
        return analyticsService.getOrderCountByStatusName();
    }

    @GetMapping("/average-order-value")
    public AverageOrderValueDTO getAverageOrderValue(){
        return analyticsService.getAverageOrderValue();
    }
}
