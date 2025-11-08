package com.github.honcharenko.ecommerceanalyticsservice.repository;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.ORDER_ITEMS;
import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.PRODUCTS;
import static org.jooq.impl.DSL.sum;

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
}
