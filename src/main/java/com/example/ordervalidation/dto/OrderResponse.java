package com.example.ordervalidation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload returned after successfully validating and creating an order")
public class OrderResponse {

    @Schema(description = "Unique generated order identifier", example = "ORD-A1B2C3D4")
    private String orderId;

    @Schema(description = "Customer name from the order request", example = "Alice Smith")
    private String customerName;

    @Schema(description = "Product name from the order request", example = "Wireless Bluetooth Headphones")
    private String productName;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price of the product", example = "49.99")
    private Double price;

    @Schema(description = "Calculated total amount (quantity * price)", example = "99.98")
    private Double totalAmount;

    @Schema(description = "Order status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Informational message", example = "Order placed successfully")
    private String message;

    public OrderResponse() {
    }

    public OrderResponse(String orderId, String customerName, String productName,
                         Integer quantity, Double price, Double totalAmount,
                         String status, String message) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.status = status;
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
