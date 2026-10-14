package com.example.ordervalidation.exception;

import com.example.ordervalidation.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Jakarta Bean Validation errors thrown when @Valid fails on @RequestBody.
     * Returns HTTP 400 Bad Request with field-level validation messages.
     *
     * Example Response Body:
     * {
     *   "timestamp": "2026-10-13T11:42:00.000Z",
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "errors": {
     *     "quantity": "Quantity must be a positive number",
     *     "customerName": "Customer name is required"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            // Keep the first validation message encountered for each field
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse response = ErrorResponse.validationError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed JSON, unparseable bodies, or incompatible data types (e.g., passing a string for a number).
     * Returns HTTP 400 Bad Request with a clean error message.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detailMessage = "Malformed JSON request body or incompatible data types";

        Throwable cause = ex.getCause();
        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            detailMessage = "Required request body is missing";
        } else if (cause instanceof JsonParseException) {
            detailMessage = "Malformed JSON syntax: please verify commas, braces, and quotes";
        } else if (cause instanceof MismatchedInputException mismatchedInputException) {
            String path = mismatchedInputException.getPathReference();
            detailMessage = "Invalid data type provided: " + (path != null ? path : "check field types");
        }

        ErrorResponse response = ErrorResponse.genericError(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                detailMessage
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for any uncaught system exceptions.
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse response = ErrorResponse.genericError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred while processing your order"
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
