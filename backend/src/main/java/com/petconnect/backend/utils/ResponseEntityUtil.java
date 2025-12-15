package com.petconnect.backend.utils;

import com.petconnect.backend.dto.ApiResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtil {

    public static <T> ResponseEntity<ApiResponseDTO<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Operation successful", data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>("Resource created successfully", data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> notFound(String message, T data) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> badRequest(String message, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> unauthorized(String message, T data) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> forbidden(String message, T data) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> internalServerError(String message, T data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> conflict(String message, T data) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> tooManyRequests(String message) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponseDTO<>(message));
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> tooManyRequests(String message, T data) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponseDTO<>(message, data));
    }

    public static <T> ResponseEntity<ApiResponseDTO<Page<T>>> page(Page<T> page) {
        return ResponseEntity.ok(new ApiResponseDTO<>("Data retrieved successfully", page));
    }

    public static <T> ResponseEntity<ApiResponseDTO<Page<T>>> page(Page<T> page, String message) {
        return ResponseEntity.ok(new ApiResponseDTO<>(message, page));
    }
}
