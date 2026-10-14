package com.example.ordervalidation.controller;

import com.example.ordervalidation.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==========================================
    // Core Functional Tests
    // ==========================================

    @Test
    @DisplayName("1. Valid request -> 201 Created with correct calculations and generated orderId")
    void shouldReturn201_WhenOrderRequestIsValid() throws Exception {
        OrderRequest request = new OrderRequest("John Doe", "Wireless Headphones", 2, 49.99);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", startsWith("ORD-")))
                .andExpect(jsonPath("$.customerName", is("John Doe")))
                .andExpect(jsonPath("$.productName", is("Wireless Headphones")))
                .andExpect(jsonPath("$.quantity", is(2)))
                .andExpect(jsonPath("$.price", is(49.99)))
                .andExpect(jsonPath("$.totalAmount", is(99.98)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.message", is("Order placed successfully")));
    }

    @Test
    @DisplayName("2. Blank customerName -> 400 Bad Request with customerName validation message")
    void shouldReturn400_WhenCustomerNameIsBlank() throws Exception {
        OrderRequest request = new OrderRequest("   ", "Mechanical Keyboard", 1, 89.99);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.customerName", is("Customer name is required")));
    }

    @Test
    @DisplayName("3. Missing productName -> 400 Bad Request with productName validation message")
    void shouldReturn400_WhenProductNameIsMissing() throws Exception {
        OrderRequest request = new OrderRequest("Jane Smith", null, 1, 120.00);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.productName", is("Product name is required")));
    }

    @Test
    @DisplayName("4. quantity = 0 -> 400 Bad Request with quantity validation message")
    void shouldReturn400_WhenQuantityIsZero() throws Exception {
        OrderRequest request = new OrderRequest("Alice Brown", "USB-C Cable", 0, 15.00);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")));
    }

    @Test
    @DisplayName("5. Negative quantity -> 400 Bad Request with quantity validation message")
    void shouldReturn400_WhenQuantityIsNegative() throws Exception {
        OrderRequest request = new OrderRequest("Bob Green", "Gaming Mouse", -3, 35.50);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")));
    }

    @Test
    @DisplayName("6. Negative price -> 400 Bad Request with price validation message")
    void shouldReturn400_WhenPriceIsNegative() throws Exception {
        OrderRequest request = new OrderRequest("Charlie White", "Smart Watch", 1, -199.99);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.price", is("Price must be greater than zero")));
    }

    @Test
    @DisplayName("7. Multiple invalid fields at once -> 400 Bad Request with all errors listed")
    void shouldReturn400_WhenMultipleFieldsAreInvalid() throws Exception {
        OrderRequest request = new OrderRequest("", "", -2, -10.00);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.customerName", is("Customer name is required")))
                .andExpect(jsonPath("$.errors.productName", is("Product name is required")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")))
                .andExpect(jsonPath("$.errors.price", is("Price must be greater than zero")));
    }

    @Test
    @DisplayName("8. Malformed JSON payload (wrong data type) -> 400 Bad Request with clean error message")
    void shouldReturn400_WhenJsonIsMalformedOrTypeMismatch() throws Exception {
        String invalidJson = """
                {
                    "customerName": "John Doe",
                    "productName": "Laptop",
                    "quantity": "not-a-number",
                    "price": 899.99
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    // ==========================================
    // Extra Edge-Case Tests
    // ==========================================

    @Test
    @DisplayName("9. Edge Case: quantity = 1 and price = 0.01 -> 201 Created with totalAmount = 0.01")
    void shouldReturn201_WhenQuantityIsOneAndPriceIsMinimumPositive() throws Exception {
        OrderRequest request = new OrderRequest("Alice Smith", "Eraser", 1, 0.01);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", startsWith("ORD-")))
                .andExpect(jsonPath("$.quantity", is(1)))
                .andExpect(jsonPath("$.price", is(0.01)))
                .andExpect(jsonPath("$.totalAmount", is(0.01)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @DisplayName("10. Edge Case: quantity = 0 -> 400 Bad Request")
    void shouldReturn400_WhenQuantityIsZeroExplicitPayload() throws Exception {
        String json = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "quantity": 0,
                    "price": 15.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")));
    }

    @Test
    @DisplayName("11. Edge Case: price = 0 -> 400 Bad Request")
    void shouldReturn400_WhenPriceIsZero() throws Exception {
        String json = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "quantity": 1,
                    "price": 0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.price", is("Price must be greater than zero")));
    }

    @Test
    @DisplayName("12. Edge Case: missing quantity -> 400 Bad Request")
    void shouldReturn400_WhenQuantityIsMissing() throws Exception {
        String json = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "price": 25.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")));
    }

    @Test
    @DisplayName("13. Edge Case: missing price -> 400 Bad Request")
    void shouldReturn400_WhenPriceIsMissing() throws Exception {
        String json = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "quantity": 2
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.price", is("Price must be greater than zero")));
    }

    @Test
    @DisplayName("14. Edge Case: customerName with only spaces -> 400 Bad Request")
    void shouldReturn400_WhenCustomerNameContainsOnlySpaces() throws Exception {
        String json = """
                {
                    "customerName": "     ",
                    "productName": "Notebook",
                    "quantity": 1,
                    "price": 10.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.customerName", is("Customer name is required")));
    }

    @Test
    @DisplayName("15. Edge Case: quantity as a string (\"two\") -> 400 Bad Request")
    void shouldReturn400_WhenQuantityIsStringTwo() throws Exception {
        String json = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "quantity": "two",
                    "price": 10.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("16. Edge Case: malformed JSON (missing closing brace) -> 400 Bad Request")
    void shouldReturn400_WhenJsonIsMissingClosingBrace() throws Exception {
        String malformedJson = """
                {
                    "customerName": "Alice Smith",
                    "productName": "Notebook",
                    "quantity": 1,
                    "price": 10.00
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("17. Edge Case: empty JSON body {} -> 400 with errors for all four fields")
    void shouldReturn400_WhenJsonBodyIsEmptyObject() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.customerName", is("Customer name is required")))
                .andExpect(jsonPath("$.errors.productName", is("Product name is required")))
                .andExpect(jsonPath("$.errors.quantity", is("Quantity must be a positive number")))
                .andExpect(jsonPath("$.errors.price", is("Price must be greater than zero")));
    }

    @Test
    @DisplayName("18. Edge Case: no request body at all -> 400 Bad Request")
    void shouldReturn400_WhenNoRequestBodyProvided() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("19. Edge Case: quantity = 1000000 -> 201 Created with the correct totalAmount")
    void shouldReturn201_WhenQuantityIsOneMillion() throws Exception {
        OrderRequest request = new OrderRequest("Wholesale Buyer", "Custom Sticker", 1000000, 2.50);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", startsWith("ORD-")))
                .andExpect(jsonPath("$.customerName", is("Wholesale Buyer")))
                .andExpect(jsonPath("$.productName", is("Custom Sticker")))
                .andExpect(jsonPath("$.quantity", is(1000000)))
                .andExpect(jsonPath("$.price", is(2.50)))
                .andExpect(jsonPath("$.totalAmount", is(2500000.00)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }
}
