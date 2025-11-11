package com.github.honcharenko.ecommerceanalyticsservice;

import com.github.honcharenko.ecommerceanalyticsservice.repository.AnalyticsRepository;
import org.jooq.*;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
public class AnalyticsRepositoryTest {

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
    private AnalyticsRepository analyticsRepository;

    @BeforeEach
    void insertPredictableCategorySales() {
        dsl.truncate(ORDER_ITEMS).restartIdentity().cascade().execute();
        dsl.truncate(ORDERS).restartIdentity().cascade().execute();
        dsl.truncate(PRODUCTS).restartIdentity().cascade().execute();
        dsl.truncate(CUSTOMERS).restartIdentity().cascade().execute();
        dsl.truncate(ORDER_STATUSES).restartIdentity().cascade().execute();

        // statuses
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
    public void testGetSalaryByCategory_ReturnsCorrectTotal() {

        BigDecimal expectedElectronicsTotal = new BigDecimal("1150.00");
        BigDecimal expectedBooksTotal = new BigDecimal("100.00");

        Result<Record2<String, BigDecimal>> result = analyticsRepository.getSalesByCategory();

        Map<String, BigDecimal> salesMap = result
                .stream()
                .collect(Collectors.toMap(
                        Record2::value1,
                        Record2::value2
        ));

        assertThat(salesMap).hasSize(2);
        assertThat(salesMap).containsKey("Electronics");
        assertThat(salesMap).containsKey("Books");

        assertThat(salesMap.get("Electronics")).isEqualByComparingTo(expectedElectronicsTotal);
        assertThat(salesMap.get("Books")).isEqualByComparingTo(expectedBooksTotal);
    }

    @Test
    public void testGetTopSellingProducts_ReturnsProductsOrderedByQuantity() {

        Result<Record3<Integer, String, BigDecimal>> result = analyticsRepository.getTopSellingProducts(10);

        assertThat(result).hasSize(3);


        assertThat(result.get(0).value2()).isEqualTo("JOOQ Guide");
        assertThat(result.get(0).value3()).isEqualByComparingTo(new BigDecimal("4"));

        assertThat(result.get(1).value2()).isEqualTo("Mouse");
        assertThat(result.get(1).value3()).isEqualByComparingTo(new BigDecimal("2"));

        assertThat(result.get(2).value2()).isEqualTo("Laptop");
        assertThat(result.get(2).value3()).isEqualByComparingTo(new BigDecimal("1"));
    }

    @Test
    public void testGetTopSpenders_ReturnsCustomersOrderedBySpend() {

        Result<Record5<Integer, String, String, String, BigDecimal>> result = analyticsRepository.getTopSpenders(10);

        assertThat(result).hasSize(1);

        Record5<Integer, String, String, String, BigDecimal> topSpender = result.get(0);

        assertThat(topSpender.value2()).isEqualTo("test@user.com");
        assertThat(topSpender.value3()).isEqualTo("Test");
        assertThat(topSpender.value4()).isEqualTo("User");

        assertThat(topSpender.value5()).isEqualByComparingTo(new BigDecimal("1100.00"));
    }

    @Test
    public void testGetStatusSummary_ReturnsOrderCountByStatus() {

        Result<Record2<String, Long>> result = analyticsRepository.getStatusSummary();

        Map<String, Long> statusMap = result
                .stream()
                .collect(Collectors.toMap(
                        Record2::value1,
                        Record2::value2
                ));

        assertThat(statusMap).hasSize(2);
        assertThat(statusMap).containsKey("Canceled");
        assertThat(statusMap).containsKey("Delivered");

        assertThat(statusMap.get("Canceled")).isEqualTo(1L);
        assertThat(statusMap.get("Delivered")).isEqualTo(1L);
    }

    @Test
    public void testGetAverageOrderValue_ReturnsCorrectAverage() {

        Record1<BigDecimal> result = analyticsRepository.getAverageOrderValue();

        BigDecimal expectedAverage = new BigDecimal("625.00");

        assertThat(result.value1()).isEqualByComparingTo(expectedAverage);
    }
}
