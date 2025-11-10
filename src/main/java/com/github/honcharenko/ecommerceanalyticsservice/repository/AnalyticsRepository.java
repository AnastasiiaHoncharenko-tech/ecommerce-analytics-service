package com.github.honcharenko.ecommerceanalyticsservice.repository;

import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.lang.Record;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.*;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.BIGINT;

@Repository
public class AnalyticsRepository {

    private final DSLContext dsl;

    public AnalyticsRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Result<Record2<String, BigDecimal>> getSalesByCategory() {

        Field<BigDecimal> categorySales = sum(
                ORDER_ITEMS.PRICE_AT_PURCHASE.mul(ORDER_ITEMS.QUANTITY)
        ).as("categorySales");

        return dsl.select(PRODUCTS.CATEGORY, categorySales)
                .from(ORDER_ITEMS)
                .join(PRODUCTS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
                .groupBy(PRODUCTS.CATEGORY)
                .orderBy(categorySales.desc())
                .fetch();
    }

    public Result<Record3<Integer, String, BigDecimal>> getTopSellingProducts() {

        Field<BigDecimal> totalProductQuantity = sum(ORDER_ITEMS.QUANTITY)
                .as("totalProductQuantity");

        return dsl.select(PRODUCTS.ID, PRODUCTS.NAME, totalProductQuantity)
                .from(PRODUCTS)
                .join(ORDER_ITEMS).on(PRODUCTS.ID.eq(ORDER_ITEMS.PRODUCT_ID))
                .groupBy(PRODUCTS.ID)
                .orderBy(totalProductQuantity.desc())
                .limit(10)
                .fetch();
    }

    public Result<Record5<Integer, String, String, String, BigDecimal>> getTopSpenders() {

        Field<BigDecimal> totalSpend = sum(ORDER_ITEMS.PRICE_AT_PURCHASE)
                .as("totalSpend");

        return dsl.select(
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
                .limit(10)
                .fetch();
    }

    public Result<Record2<String, Long>> getStatusSummary() {

        Field<Long> orderCount = count(ORDERS.ID)
                .cast(BIGINT)
                .as("orderCount");

        return dsl.select(
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
                .as("orderTotal");

        var orderTotalsSubquery = dsl.select(orderTotal)
                .from(ORDER_ITEMS)
                .groupBy(ORDER_ITEMS.ORDER_ID)
                .asTable("order_totals_cte");

        return dsl.select(avg(orderTotalsSubquery.field(orderTotal)))
                .from(orderTotalsSubquery)
                .fetchOptional()
                .orElseThrow(() -> new RuntimeException("Fail to get Average Order Value"));

    }

}
