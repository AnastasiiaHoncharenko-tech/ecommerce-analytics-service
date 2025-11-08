package com.github.honcharenko.ecommerceanalyticsservice;

import com.github.javafaker.Faker;
import org.jooq.DSLContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.*;

@Profile("!test")
@Component
public class DataSeeder implements CommandLineRunner {

    private final DSLContext dsl;
    private final Faker faker = new Faker();

    public DataSeeder(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void run(String... args) throws Exception {

        // Truncating all tables and resetting sequences to 1

        dsl.truncate(ORDER_ITEMS).restartIdentity().cascade().execute();
        dsl.truncate(ORDERS).restartIdentity().cascade().execute();
        dsl.truncate(CUSTOMERS).restartIdentity().cascade().execute();
        dsl.truncate(ORDER_STATUSES).restartIdentity().cascade().execute();
        dsl.truncate(PRODUCTS).restartIdentity().cascade().execute();


        // Inserting statuses first

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Processing")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Canceled")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Shipped")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Delivered")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Refunded")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Returned")
                .execute();

        dsl.insertInto(ORDER_STATUSES)
                .set(ORDER_STATUSES.STATUS_NAME, "Pending")
                .execute();

        // Inserting new products

        for (int i = 0; i < 5; i++) {
            dsl.insertInto(PRODUCTS)
                    .set(PRODUCTS.NAME, faker.commerce().productName())
                    .set(PRODUCTS.PRICE, new BigDecimal(faker.commerce().price()))
                    .set(PRODUCTS.CATEGORY, faker.commerce().department())
                    .execute();
        }

        // Inserting new customers

        for (int i = 0; i < 5; i++) {
            dsl.insertInto(CUSTOMERS)
                    .set(CUSTOMERS.FIRST_NAME, faker.name().firstName())
                    .set(CUSTOMERS.LAST_NAME, faker.name().lastName())
                    .set(CUSTOMERS.EMAIL, faker.internet().emailAddress())
                    .set(CUSTOMERS.JOIN_DATE, faker.date().past(365, TimeUnit.DAYS)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())
                    .execute();
        }


        // Inserting new orders

        for (int i = 0; i < 5; i++) {
            dsl.insertInto(ORDERS)
                    .set(ORDERS.CUSTOMER_ID, faker.number().numberBetween(1, 6))
                    .set(ORDERS.ORDER_DATE, faker.date().past(365, TimeUnit.DAYS)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())
                    .set(ORDERS.STATUS_ID, faker.number().numberBetween(1, 8))
                    .execute();
        }

        // Inserting new order items

        for (int i = 0; i < 5; i++) {
            dsl.insertInto(ORDER_ITEMS)
                    .set(ORDER_ITEMS.ORDER_ID, faker.number().numberBetween(1, 6))
                    .set(ORDER_ITEMS.PRODUCT_ID, faker.number().numberBetween(1, 6))
                    .set(ORDER_ITEMS.QUANTITY, faker.number().numberBetween(1, 999))
                    .set(ORDER_ITEMS.PRICE_AT_PURCHASE, new BigDecimal(faker.commerce().price()))
                    .execute();
        }
    }
}