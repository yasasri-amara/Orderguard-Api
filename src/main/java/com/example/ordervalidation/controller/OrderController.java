package com.example.ordervalidation.controller;

import com.example.ordervalidation.dto.ErrorResponse;
import com.example.ordervalidation.dto.OrderRequest;
import com.example.ordervalidation.dto.OrderResponse;
import com.example.ordervalidation.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for placing and validating customer orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates and validates a customer order.
     * Uses @Valid @RequestBody to enforce bean validation rules before processing.
     *
     * @param orderRequest the validated order request DTO
     * @return 201 Created with OrderResponse on success
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Place a new order",
            description = "Validates the order request body using Jakarta Bean Validation. " +
                          "Returns 201 Created with the computed order details if validation succeeds, " +
                          "or 400 Bad Request with field-level errors if validation fails."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created and confirmed successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"orderId\":\"ORD-7D3A2F1C\",\"customerName\":\"Alice Smith\",\"productName\":\"Wireless Bluetooth Headphones\",\"quantity\":2,\"price\":49.99,\"totalAmount\":99.98,\"status\":\"CONFIRMED\",\"message\":\"Order placed successfully\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure or malformed request payload",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"timestamp\":\"2026-10-13T11:42:00.000Z\",\"status\":400,\"error\":\"Validation Failed\",\"errors\":{\"customerName\":\"Customer name is required\",\"quantity\":\"Quantity must be a positive number\"}}"
                            )
                    )
            )
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.processOrder(orderRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
