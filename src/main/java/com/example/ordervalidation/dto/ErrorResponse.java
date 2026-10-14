package com.example.ordervalidation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response payload for validation or request processing failures")
public class ErrorResponse {

    @Schema(description = "Timestamp when error occurred in ISO-8601 format", example = "2026-10-13T11:42:00.000Z")
    private String timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "High-level error classification", example = "Validation Failed")
    private String error;

    @Schema(description = "Descriptive error message (present for malformed requests)", example = "Malformed JSON request or invalid data types")
    private String message;

    @Schema(description = "Field-specific validation failure messages", example = "{\"quantity\": \"Quantity must be a positive number\"}")
    private Map<String, String> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse validationError(int status, String error, Map<String, String> errors) {
        return new ErrorResponse(Instant.now().toString(), status, error, null, errors);
    }

    public static ErrorResponse genericError(int status, String error, String message) {
        return new ErrorResponse(Instant.now().toString(), status, error, message, null);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
