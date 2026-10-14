# Postman & cURL Test Payloads: Online Shopping Order Validation API

This document provides sample JSON payloads, expected HTTP status codes, and response bodies for testing the Order Validation API (`POST /api/orders`).

---

## 📌 Endpoint Information

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/orders`
- **Headers**:
  - `Content-Type: application/json`
  - `Accept: application/json`

---

## 🟢 Case 1: Valid Order Request (Happy Path)

### Description
All required fields are present and satisfy validation constraints:
- `customerName` is non-blank.
- `productName` is non-blank.
- `quantity` is a positive integer (> 0).
- `price` is a positive double (> 0.0).

### Request Payload
```json
{
  "customerName": "Alice Smith",
  "productName": "Wireless Noise Canceling Headphones",
  "quantity": 2,
  "price": 49.99
}
```

### Expected HTTP Status: `201 Created`
### Expected Response Body
```json
{
  "orderId": "ORD-XXXXXXXX",
  "customerName": "Alice Smith",
  "productName": "Wireless Noise Canceling Headphones",
  "quantity": 2,
  "price": 49.99,
  "totalAmount": 99.98,
  "status": "CONFIRMED",
  "message": "Order placed successfully"
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Smith",
    "productName": "Wireless Noise Canceling Headphones",
    "quantity": 2,
    "price": 49.99
  }'
```

---

## 🔴 Case 2: Blank Customer Name

### Description
`customerName` contains only whitespace characters (`"   "`), violating `@NotBlank`.

### Request Payload
```json
{
  "customerName": "   ",
  "productName": "Mechanical Keyboard",
  "quantity": 1,
  "price": 89.99
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "customerName": "Customer name is required"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "   ",
    "productName": "Mechanical Keyboard",
    "quantity": 1,
    "price": 89.99
  }'
```

---

## 🔴 Case 3: Missing / Null Product Name

### Description
`productName` is omitted or passed as `null`, violating `@NotBlank`.

### Request Payload
```json
{
  "customerName": "Bob Johnson",
  "quantity": 1,
  "price": 120.00
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "productName": "Product name is required"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Bob Johnson",
    "quantity": 1,
    "price": 120.00
  }'
```

---

## 🔴 Case 4: Quantity Equals Zero

### Description
`quantity` is `0`, violating `@Positive` (which strictly requires value > 0).

### Request Payload
```json
{
  "customerName": "Charlie Brown",
  "productName": "USB-C Fast Charging Cable",
  "quantity": 0,
  "price": 14.99
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "quantity": "Quantity must be a positive number"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Charlie Brown",
    "productName": "USB-C Fast Charging Cable",
    "quantity": 0,
    "price": 14.99
  }'
```

---

## 🔴 Case 5: Negative Quantity

### Description
`quantity` is negative (`-5`), violating `@Positive`.

### Request Payload
```json
{
  "customerName": "Diana Prince",
  "productName": "Ergonomic Office Chair",
  "quantity": -5,
  "price": 199.50
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "quantity": "Quantity must be a positive number"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Diana Prince",
    "productName": "Ergonomic Office Chair",
    "quantity": -5,
    "price": 199.50
  }'
```

---

## 🔴 Case 6: Negative Price

### Description
`price` is negative (`-49.99`), violating `@Positive`.

### Request Payload
```json
{
  "customerName": "Evan Wright",
  "productName": "Smart Fitness Watch",
  "quantity": 1,
  "price": -49.99
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "price": "Price must be greater than zero"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Evan Wright",
    "productName": "Smart Fitness Watch",
    "quantity": 1,
    "price": -49.99
  }'
```

---

## 🔴 Case 7: Multiple Invalid Fields at Once

### Description
All four fields contain invalid inputs: empty `customerName`, empty `productName`, negative `quantity`, and negative `price`.
Spring Boot's validation engine collects all errors and returns them in one structured response.

### Request Payload
```json
{
  "customerName": "",
  "productName": "",
  "quantity": -2,
  "price": -10.00
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "customerName": "Customer name is required",
    "productName": "Product name is required",
    "quantity": "Quantity must be a positive number",
    "price": "Price must be greater than zero"
  }
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "",
    "productName": "",
    "quantity": -2,
    "price": -10.00
  }'
```

---

## 🔴 Case 8: Incompatible Data Type (Malformed JSON Payload)

### Description
`quantity` is supplied as a String (`"two"`) instead of an Integer. Caught by `HttpMessageNotReadableException`.

### Request Payload
```json
{
  "customerName": "Frank Miller",
  "productName": "Mechanical Keyboard",
  "quantity": "two",
  "price": 89.99
}
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Malformed JSON Request",
  "message": "Invalid data type provided: com.example.ordervalidation.dto.OrderRequest[\"quantity\"]"
}
```

### cURL Command
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Frank Miller",
    "productName": "Mechanical Keyboard",
    "quantity": "two",
    "price": 89.99
  }'
```

---

## 🔴 Case 9: Malformed JSON Syntax

### Description
Malformed JSON with a missing closing brace and invalid trailing comma.

### Request Payload
```text
{
  "customerName": "Frank Miller",
  "productName": "Mechanical Keyboard"
```

### Expected HTTP Status: `400 Bad Request`
### Expected Response Body
```json
{
  "timestamp": "2026-10-13T11:42:00.000Z",
  "status": 400,
  "error": "Malformed JSON Request",
  "message": "Malformed JSON syntax: please verify commas, braces, and quotes"
}
```
