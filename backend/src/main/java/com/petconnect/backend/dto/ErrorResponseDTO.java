package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;      // "Validation errors"
    private Map<String, String> errors; // List of specific error messages (e.g., "Field X is required")
}