# Viva & Interview Preparation Guide: Spring Boot Request Validation

This guide provides clear, structured, and exam-ready answers to frequently asked viva and technical interview questions on Spring Boot request validation.

---

## Question 1: What is the purpose of the `@Valid` annotation in Spring Boot?

### 1. Direct Definition & Purpose
The `@Valid` annotation belongs to the standard **Jakarta Bean Validation specification** (`jakarta.validation.Valid`, formerly `javax.validation.Valid`). 

In Spring Boot REST applications, `@Valid` is placed before a method argument (such as `@Valid @RequestBody OrderRequest request`) in a `@RestController` to **instruct Spring's `DispatcherServlet` and validator infrastructure to automatically trigger constraint validation on the incoming request payload before executing any controller method business logic**.

```java
@PostMapping("/api/orders")
public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
    // This code only runs if orderRequest passes all validation rules
    OrderResponse response = orderService.processOrder(orderRequest);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
```

---

### 2. How Validation Works Internally (The Request Lifecycle)

1. **HTTP Request Arrival**: A client sends an HTTP `POST` request with a JSON body.
2. **Deserialization**: Spring's `HttpMessageConverter` (Jackson) parses the JSON into the Java DTO (`OrderRequest`).
3. **Validation Trigger**: Because `@Valid` is present, Spring delegates the populated DTO to the registered validation provider (Hibernate Validator).
4. **Constraint Verification**: Hibernate Validator checks every annotation placed on the DTO fields (`@NotBlank`, `@NotNull`, `@Positive`, etc.).
5. **Outcome Branching**:
   - **If Valid**: Validation succeeds cleanly. The controller handler method executes.
   - **If Invalid**: A `MethodArgumentNotValidException` is immediately thrown containing a `BindingResult` with all collected `FieldError` instances. The controller method logic is **never executed**, safeguarding backend integrity.
6. **Global Exception Handling**: A `@RestControllerAdvice` class intercepts `MethodArgumentNotValidException` and returns a formatted HTTP `400 Bad Request` response with field-level error messages.

---

### 3. Cascading (Nested Object) Validation
`@Valid` is also critical for **cascaded validation**. If an `OrderRequest` contains a nested object (e.g., `Address address`), placing `@Valid` on the nested field instructs Hibernate Validator to descend into and validate the nested object's fields:

```java
public class OrderRequest {
    @NotNull
    @Valid // Validates fields inside Address (e.g., street, city, zipCode)
    private Address shippingAddress;
}
```

---

### 4. Key Distinction: `@Valid` vs. `@Validated`

| Feature | `@Valid` | `@Validated` |
| :--- | :--- | :--- |
| **Origin / Package** | Standard Jakarta EE (`jakarta.validation.Valid`) | Spring Framework (`org.springframework.validation.annotation.Validated`) |
| **Validation Groups** | Does **not** support validation groups | Supports **Validation Groups** (e.g., different rules for Create vs. Update) |
| **Where to Apply** | Method parameters, fields, getters, constructors | Method parameters, classes (e.g., on `@Service` or `@RestController` for path variables) |
| **Target in Method Validation** | Primarily DTO bodies (`@RequestBody`) | Required on class level for `@RequestParam` and `@PathVariable` validation |

---

## Question 2: What is the difference between `@NotNull`, `@NotBlank`, and `@Positive`?

### 1. Detailed Breakdown

#### 1. `@NotNull`
- **Applicable Types**: Any Java object type (`String`, `Integer`, `Double`, `List`, custom objects, etc.).
- **Rule**: Checks that the value is strictly **not null** (`value != null`).
- **Limitation for Strings**: It allows empty strings (`""`) and strings with only whitespace (`"   "`).
- **Limitation for Collections**: It allows empty lists (`[]`).

#### 2. `@NotBlank`
- **Applicable Types**: **`CharSequence` / `String` ONLY**. (Applying to `Integer` or `Double` causes an `UnexpectedTypeException` during startup/validation).
- **Rule**: Checks that the value is:
  1. Not null (`value != null`), AND
  2. The trimmed string length is greater than 0 (`value.trim().length() > 0`).
- **Behavior**: Rejects `null`, `""`, and `"   "`.

#### 3. `@Positive`
- **Applicable Types**: Numeric types (`byte`, `short`, `int`, `long`, `float`, `double`, `BigDecimal`, `BigInteger`, and their wrapper classes).
- **Rule**: Checks that the numeric value is strictly **greater than zero** (`value > 0`).
- **Crucial Spec Detail**: Under the Bean Validation specification, **`null` values are considered valid by `@Positive`**. Therefore, to enforce both non-null and positive rules on an `Integer` or `Double`, you must pair `@NotNull` with `@Positive`.

---

### 2. Comprehensive Comparison Table

| Annotation | Target Java Data Types | `null` | `""` (Empty String) | `"   "` (Whitespace) | `0` (Zero) | `-5` (Negative) | `10` (Positive) | Typical Use Case |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **`@NotNull`** | Any `Object` (`Integer`, `Date`, `UUID`, `String`, etc.) | ❌ Invalid |  Valid |  Valid |  Valid |  Valid |  Valid | Foreign keys, IDs, Booleans, Enums, Dates |
| **`@NotBlank`** | `String` / `CharSequence` **only** | ❌ Invalid | ❌ Invalid | ❌ Invalid | N/A (Compile/Runtime Type error) | N/A | N/A | Customer names, product titles, addresses, passwords |
| **`@Positive`** | Numeric types (`Integer`, `Double`, `BigDecimal`, etc.) |  Valid *(by spec)* | N/A | N/A | ❌ Invalid | ❌ Invalid |  Valid | Quantities, prices, salaries, retry counts |

> 💡 **Key Takeaway**: 
> - For text: Use **`@NotBlank`** instead of `@NotNull` to avoid allowing invisible/blank strings.
> - For numbers: Combine **`@NotNull` + `@Positive`** so missing values and non-positive numbers are both rejected.

---

### 3. Concrete Code Examples

#### Example DTO Demonstrating Correct Usage:
```java
package com.example.ordervalidation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequest {

    // 1. @NotBlank: Text field cannot be null, empty, or pure whitespace
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Product name is required")
    private String productName;

    // 2. @NotNull + @Positive: Number must exist AND be strictly > 0
    @NotNull(message = "Quantity must be a positive number")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    // 3. @NotNull + @Positive: Double precision currency must exist AND be > 0.0
    @NotNull(message = "Price must be greater than zero")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    // Constructors, Getters, and Setters...
}
```

#### Behavior Matrix with Sample Payloads:

```json
// Case A: Fails @NotBlank (whitespace only)
{
  "customerName": "   ",
  "productName": "Keyboard",
  "quantity": 1,
  "price": 50.0
}
// Error: "Customer name is required"

// Case B: Fails @Positive (quantity is zero)
{
  "customerName": "John Doe",
  "productName": "Keyboard",
  "quantity": 0,
  "price": 50.0
}
// Error: "Quantity must be a positive number"

// Case C: Fails @NotNull (price is omitted / null)
{
  "customerName": "John Doe",
  "productName": "Keyboard",
  "quantity": 1
}
// Error: "Price must be greater than zero"
```

---

## Question 3 (Bonus): Why use `@RestControllerAdvice` for validation exceptions instead of handling them inside the controller?

1. **Separation of Concerns**: Controllers focus purely on routing and business coordination; error handling is centralized in one place.
2. **DRY Principle (Don't Repeat Yourself)**: Avoids writing repetitive `try-catch` blocks or `BindingResult` checks inside every single controller method across the application.
3. **Consistent Error Schema**: Guarantees that clients always receive a uniform, predictable JSON error structure across all REST endpoints in the system.
