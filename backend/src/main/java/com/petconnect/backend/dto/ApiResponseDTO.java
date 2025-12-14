package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseDTO<T> {
    private String message;
    private T data;

    // Constructor with message and data
    public ApiResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Convenience constructor for message-only responses
    public ApiResponseDTO(String message) {
        this.message = message;
        this.data = null;
    }
}