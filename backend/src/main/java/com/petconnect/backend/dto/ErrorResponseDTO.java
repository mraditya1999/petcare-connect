package com.petconnect.backend.dto;

import java.util.Map;

public class ErrorResponseDTO {
    private String message;      // "Validation errors"
    private Map<String, String> errors; // List of specific error messages (e.g., "Field X is required")

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String message, Map<String, String> errors) { // Update Constructor
        this.message = message;
        this.errors = errors;
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