package com.example.ordervalidation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Order placement request payload containing customer and product details")
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    @Schema(description = "Full name of the customer", example = "Alice Smith", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @NotBlank(message = "Product name is required")
    @Schema(description = "Name of the ordered product", example = "Wireless Bluetooth Headphones", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;

    @NotNull(message = "Quantity must be a positive number")
    @Positive(message = "Quantity must be a positive number")
    @Schema(description = "Quantity of products ordered (must be greater than 0)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull(message = "Price must be greater than zero")
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "Unit price of the product (must be greater than 0.0)", example = "49.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    public OrderRequest() {
    }

    public OrderRequest(String customerName, String productName, Integer quantity, Double price) {
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "customerName='" + customerName + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
