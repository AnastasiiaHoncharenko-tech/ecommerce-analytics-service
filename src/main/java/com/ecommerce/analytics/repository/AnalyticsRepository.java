package com.ecommerce.analytics.repository;

import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.ecommerce.analytics.jooq.Tables.*;
import static com.ecommerce.analytics.constants.AnalyticsConstants.*;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.BIGINT;

@Repository
public class AnalyticsRepository {

    private final DSLContext context;

    public AnalyticsRepository(DSLContext context) {
        this.context = context;
    }

    public Result<Record2<String, BigDecimal>> getSalesByCategory() {
        Field<BigDecimal> categorySales = sum(
                ORDER_ITEMS.PRICE_AT_PURCHASE.mul(ORDER_ITEMS.QUANTITY)
        ).as(CATEGORY_SALES_FIELD);

        return context.select(PRODUCTS.CATEGORY, categorySales)
                .from(ORDER_ITEMS)
                .join(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
                .groupBy(PRODUCTS.CATEGORY)
                .orderBy(categorySales.desc())
                .fetch();
    }

    public Result<Record3<Integer, String, BigDecimal>> getTopSellingProducts(int limit) {
        Field<BigDecimal> totalProductQuantity = sum(ORDER_ITEMS.QUANTITY)
                .as(TOTAL_PRODUCT_QUANTITY_FIELD);

        return context.select(PRODUCTS.ID, PRODUCTS.NAME, totalProductQuantity)
                .from(PRODUCTS)
                .join(ORDER_ITEMS).on(PRODUCTS.ID.eq(ORDER_ITEMS.PRODUCT_ID))
                .groupBy(PRODUCTS.ID)
                .orderBy(totalProductQuantity.desc())
                .limit(limit)
                .fetch();
    }

    public Result<Record5<Integer, String, String, String, BigDecimal>> getTopSpenders(int limit) {
        Field<BigDecimal> totalSpend = sum(ORDER_ITEMS.PRICE_AT_PURCHASE)
                .as(TOTAL_SPEND_FIELD);

        return context.select(
                CUSTOMERS.ID,
                CUSTOMERS.EMAIL,
                CUSTOMERS.FIRST_NAME,
                CUSTOMERS.LAST_NAME,
                totalSpend)
                .from(CUSTOMERS)
                .join(ORDERS).on(ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID))
                .join(ORDER_ITEMS).on(ORDER_ITEMS.ORDER_ID.eq(ORDERS.ID))
                .groupBy(CUSTOMERS.ID)
                .orderBy(totalSpend.desc())
                .limit(limit)
                .fetch();
    }

    public Result<Record2<String, Long>> getOrderCountByStatusName() {
        Field<Long> orderCount = count(ORDERS.ID)
                .cast(BIGINT)
                .as(ORDER_COUNT_FIELD);

        return context.select(
                ORDER_STATUSES.STATUS_NAME,
                orderCount)
                .from(ORDER_STATUSES)
                .join(ORDERS).on(ORDERS.STATUS_ID.eq(ORDER_STATUSES.ID))
                .groupBy(ORDER_STATUSES.ID)
                .orderBy(orderCount.desc())
                .fetch();
    }

    public Record1<BigDecimal> getAverageOrderValue() {
        Field<BigDecimal> orderTotal = sum(ORDER_ITEMS.QUANTITY.mul(ORDER_ITEMS.PRICE_AT_PURCHASE))
                .as(ORDER_TOTAL_FIELD);

        var orderTotalsSubquery = context.select(orderTotal)
                .from(ORDER_ITEMS)
                .groupBy(ORDER_ITEMS.ORDER_ID)
                .asTable(ORDER_TOTALS_CTE);

        return context.select(avg(orderTotalsSubquery.field(orderTotal)))
                .from(orderTotalsSubquery)
                .fetchOptional()
                .orElseThrow(() -> new RuntimeException(AVERAGE_ORDER_VALUE_ERROR));
    }

    public Result<Record5<Integer, String, String, BigDecimal, Integer>> getProductRankByCategory(int limit) {
        Field<BigDecimal> totalQuantitySold = sum(ORDER_ITEMS.QUANTITY)
                .as(TOTAL_PRODUCT_QUANTITY_FIELD);

        Field<Integer> categoryRank = rowNumber()
                .over(partitionBy(PRODUCTS.CATEGORY)
                        .orderBy(totalQuantitySold.desc()))
                .as(CATEGORY_RANK_FIELD);

        var rankedProducts = context.select(
                        PRODUCTS.ID,
                        PRODUCTS.NAME,
                        PRODUCTS.CATEGORY,
                        totalQuantitySold,
                        categoryRank)
                .from(PRODUCTS)
                .join(ORDER_ITEMS).on(PRODUCTS.ID.eq(ORDER_ITEMS.PRODUCT_ID))
                .groupBy(PRODUCTS.ID, PRODUCTS.CATEGORY)
                .asTable(RANKED_PRODUCTS_CTE);

        return context.select(
                        rankedProducts.field(PRODUCTS.ID),
                        rankedProducts.field(PRODUCTS.NAME),
                        rankedProducts.field(PRODUCTS.CATEGORY),
                        rankedProducts.field(totalQuantitySold),
                        rankedProducts.field(categoryRank))
                .from(rankedProducts)
                .where(rankedProducts.field(categoryRank).le(limit))
                .orderBy(rankedProducts.field(PRODUCTS.CATEGORY), rankedProducts.field(categoryRank))
                .fetch();
    }

}
