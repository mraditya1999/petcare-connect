package com.petconnect.backend.utils;

import com.petconnect.backend.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Enhanced error response utility for consistent error handling.
 * Provides methods to create various types of error responses.
 */
public final class ErrorResponseUtils {

    private ErrorResponseUtils() {
        // Utility class
    }

    /**
     * Create error response with status.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponseDTO<>(message));
    }

    /**
     * Create error response with status and data.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> error(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponseDTO<>(message, data));
    }

    /**
     * Create bad request error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Create bad request error with data.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String message, T data) {
        return error(message, data, HttpStatus.BAD_REQUEST);
    }

    /**
     * Create unauthorized error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Create forbidden error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Create not found error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Create conflict error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }

    /**
     * Create internal server error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> internalServerError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create too many requests error.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> tooManyRequests(String message) {
        return error(message, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Create validation error with field details.
     */
    public static ResponseEntity<ApiResponseDTO<Object>> validationError(java.util.Map<String, String> fieldErrors) {
        return badRequest("Validation failed", fieldErrors);
    }

    /**
     * Create validation error with single field.
     */
    public static ResponseEntity<ApiResponseDTO<Object>> validationError(String field, String message) {
        java.util.Map<String, String> errors = java.util.Map.of(field, message);
        return validationError(errors);
    }
}