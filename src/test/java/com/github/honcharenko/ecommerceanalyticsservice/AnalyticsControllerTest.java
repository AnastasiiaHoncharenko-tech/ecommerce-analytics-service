package com.github.honcharenko.ecommerceanalyticsservice;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static com.github.anastasiia.ecommerceanalyticsservice.jooq.Tables.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser
public class AnalyticsControllerTest {

    @Container
    @SuppressWarnings("resource")
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
    private MockMvc mockMvc;

    @Autowired
    private DSLContext dsl;

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
    public void testGetSalesByCategory_ReturnsJsonArray() throws Exception {

        mockMvc.perform(get("/analytics/salesByCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].totalSales").value(1150.00))
                .andExpect(jsonPath("$[1].category").value("Books"))
                .andExpect(jsonPath("$[1].totalSales").value(100.00));
    }

    @Test
    public void testGetTopSellingProducts_ReturnsJsonArray() throws Exception {

        String requestBody = "{\"limit\": 10}";

        mockMvc.perform(post("/analytics/topSellingProducts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].productName").value("JOOQ Guide"))
                .andExpect(jsonPath("$[0].totalQuantitySold").value(4))
                .andExpect(jsonPath("$[1].productName").value("Mouse"))
                .andExpect(jsonPath("$[1].totalQuantitySold").value(2))
                .andExpect(jsonPath("$[2].productName").value("Laptop"))
                .andExpect(jsonPath("$[2].totalQuantitySold").value(1));
    }

    @Test
    public void testGetTopSpenders_ReturnsJsonArray() throws Exception {

        String requestBody = "{\"limit\": 10}";

        mockMvc.perform(post("/analytics/topSpenders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("test@user.com"))
                .andExpect(jsonPath("$[0].fullName").value("Test User"))
                .andExpect(jsonPath("$[0].totalSpend").value(1100.00));
    }

    @Test
    public void testGetStatusSummary_ReturnsJsonArray() throws Exception {

        mockMvc.perform(get("/analytics/statusSummary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetAverageOrderValue_ReturnsJsonObject() throws Exception {

        mockMvc.perform(get("/analytics/averageOrderValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageOrderValue").value(625.00));
    }
}