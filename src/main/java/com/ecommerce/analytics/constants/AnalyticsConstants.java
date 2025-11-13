package com.ecommerce.analytics.constants;

public final class AnalyticsConstants {

    private AnalyticsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = 100;
    public static final int MIN_LIMIT = 1;

    public static final String CATEGORY_SALES_FIELD = "categorySales";
    public static final String TOTAL_PRODUCT_QUANTITY_FIELD = "totalProductQuantity";
    public static final String TOTAL_SPEND_FIELD = "totalSpend";
    public static final String ORDER_COUNT_FIELD = "orderCount";
    public static final String ORDER_TOTAL_FIELD = "orderTotal";
    public static final String ORDER_TOTALS_CTE = "order_totals_cte";
    public static final String RANKED_PRODUCTS_CTE = "ranked_products";
    public static final String CATEGORY_RANK_FIELD = "categoryRank";

    public static final String AVERAGE_ORDER_VALUE_ERROR = "Failed to get Average Order Value";
}
