package com.spring.petcareConnect.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for all endpoints
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public CustomApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.success = data != null;
    }

    public CustomApiResponse(boolean success, String message, T data) {
        this.data = data;
        this.message = message;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    // Static factory helpers for common responses
    public static <T> CustomApiResponse<T> success(String message, T data) {
        return new CustomApiResponse<>(true, message, data);
    }

    public static <T> CustomApiResponse<T> error(String message) {
        return new CustomApiResponse<>(false, message, null);
    }

    public static <T> CustomApiResponse<T> created(String message, T data) {
        return new CustomApiResponse<>(true, message, data);
    }
}