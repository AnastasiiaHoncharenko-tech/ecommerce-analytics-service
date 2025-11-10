package com.github.honcharenko.ecommerceanalyticsservice;

import com.github.honcharenko.ecommerceanalyticsservice.DTO.*;
import com.github.honcharenko.ecommerceanalyticsservice.service.AnalyticsService;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
public class AnalyticsServiceTest {

    @SuppressWarnings("resource")
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init_scheme.sql");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jooq.sql-dialect", () -> "POSTGRES");
    }

    @Autowired
    private DSLContext dsl;

    @Autowired
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        dsl.truncate(ORDER_ITEMS).restartIdentity().cascade().execute();
        dsl.truncate(ORDERS).restartIdentity().cascade().execute();
        dsl.truncate(PRODUCTS).restartIdentity().cascade().execute();
        dsl.truncate(CUSTOMERS).restartIdentity().cascade().execute();
        dsl.truncate(ORDER_STATUSES).restartIdentity().cascade().execute();

        // Statuses
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

        // Products
        int p_laptop = dsl.insertInto(PRODUCTS, PRODUCTS.NAME, PRODUCTS.PRICE, PRODUCTS.CATEGORY)
                .values("Laptop", new BigDecimal("1000.00"), "Electronics")
                .returning(PRODUCTS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test product id"))
                .getId();

        int p_mouse = dsl.insertInto(PRODUCTS, PRODUCTS.NAME, PRODUCTS.PRICE, PRODUCTS.CATEGORY)
                .values("Mouse", new BigDecimal("75.00"), "Electronics")
                .returning(PRODUCTS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test product id"))
                .getId();

        int p_book = dsl.insertInto(PRODUCTS, PRODUCTS.NAME, PRODUCTS.PRICE, PRODUCTS.CATEGORY)
                .values("JOOQ Guide", new BigDecimal("25.00"), "Books")
                .returning(PRODUCTS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test product id"))
                .getId();

        // Customer
        int c_user = dsl.insertInto(CUSTOMERS, CUSTOMERS.FIRST_NAME, CUSTOMERS.LAST_NAME, CUSTOMERS.EMAIL, CUSTOMERS.JOIN_DATE)
                .values("Test", "User", "test@user.com", LocalDate.now())
                .returning(CUSTOMERS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test customer id"))
                .getId();

        // Orders
        int o1 = dsl.insertInto(ORDERS, ORDERS.CUSTOMER_ID, ORDERS.ORDER_DATE, ORDERS.STATUS_ID)
                .values(c_user, LocalDate.now(), 2)
                .returning(ORDERS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test order id"))
                .getId();

        int o2 = dsl.insertInto(ORDERS, ORDERS.CUSTOMER_ID, ORDERS.ORDER_DATE, ORDERS.STATUS_ID)
                .values(c_user, LocalDate.now(), 4)
                .returning(ORDERS.ID)
                .fetchOptional()
                .orElseThrow(() -> new NoSuchElementException("Fail to get new test order id"))
                .getId();

        // Order Items
        dsl.insertInto(ORDER_ITEMS)
                .columns(ORDER_ITEMS.ORDER_ID, ORDER_ITEMS.PRODUCT_ID, ORDER_ITEMS.QUANTITY, ORDER_ITEMS.PRICE_AT_PURCHASE)
                .values(o1, p_laptop, 1, new BigDecimal("1000.00"))
                .values(o1, p_mouse, 2, new BigDecimal("75.00"))
                .values(o2, p_book, 4, new BigDecimal("25.00"))
                .execute();
    }

    @Test
    public void testGetSalesByCategory_ReturnsListOfDTOs() {

        List<SalesByCategoryDTO> result = analyticsService.getSalesByCategory();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
        assertThat(result.get(0).getTotalSales()).isEqualByComparingTo(new BigDecimal("1150.00"));
        assertThat(result.get(1).getCategory()).isEqualTo("Books");
        assertThat(result.get(1).getTotalSales()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    public void testGetTopSellingProducts_ReturnsListOfDTOs() {

        List<TopSellingProductsDTO> result = analyticsService.getTopSellingProducts();

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getProductName()).isEqualTo("JOOQ Guide");
        assertThat(result.get(0).getTotalQuantitySold()).isEqualTo(4L);

        assertThat(result.get(1).getProductName()).isEqualTo("Mouse");
        assertThat(result.get(1).getTotalQuantitySold()).isEqualTo(2L);

        assertThat(result.get(2).getProductName()).isEqualTo("Laptop");
        assertThat(result.get(2).getTotalQuantitySold()).isEqualTo(1L);
    }

    @Test
    public void testGetTopSenders_ReturnsListOfDTOs() {

        List<TopSendersDTO> result = analyticsService.getTopSenders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@user.com");
        assertThat(result.get(0).getFullName()).isEqualTo("Test User");
        assertThat(result.get(0).getTotalSpend()).isEqualByComparingTo(new BigDecimal("1100.00"));
    }

    @Test
    public void testGetStatusSummary_ReturnsListOfDTOs() {

        List<StatusSummaryDTO> result = analyticsService.getStatusSummary();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatusName()).isIn("Canceled", "Delivered");
        assertThat(result.get(0).getOrderCount()).isEqualTo(1L);
        assertThat(result.get(1).getStatusName()).isIn("Canceled", "Delivered");
        assertThat(result.get(1).getOrderCount()).isEqualTo(1L);
    }

    @Test
    public void testGetAverageOrderValue_ReturnsDTO() {

        AverageOrderValueDTO result = analyticsService.getAverageOrderValue();

        BigDecimal expectedAverage = new BigDecimal("625.00");
        assertThat(result.getAverageOrderValue()).isEqualByComparingTo(expectedAverage);
    }
}