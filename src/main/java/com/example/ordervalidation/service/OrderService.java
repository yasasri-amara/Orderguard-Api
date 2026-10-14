package com.example.ordervalidation.service;

import com.example.ordervalidation.dto.OrderRequest;
import com.example.ordervalidation.dto.OrderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class OrderService {

    /**
     * Processes a validated order request: calculates total amount, generates an order ID,
     * and constructs the confirmed OrderResponse.
     *
     * @param request the validated OrderRequest
     * @return the processed OrderResponse
     */
    public OrderResponse processOrder(OrderRequest request) {
        // Generate a unique order identifier
        String orderId = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        // Compute totalAmount = quantity * price, rounded to 2 decimal places
        BigDecimal quantity = BigDecimal.valueOf(request.getQuantity());
        BigDecimal price = BigDecimal.valueOf(request.getPrice());
        BigDecimal total = quantity.multiply(price).setScale(2, RoundingMode.HALF_UP);
        Double totalAmount = total.doubleValue();

        return new OrderResponse(
                orderId,
                request.getCustomerName().trim(),
                request.getProductName().trim(),
                request.getQuantity(),
                request.getPrice(),
                totalAmount,
                "CONFIRMED",
                "Order placed successfully"
        );
    }
}
