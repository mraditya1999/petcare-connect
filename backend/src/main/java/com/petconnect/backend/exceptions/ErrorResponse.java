package com.petconnect.backend.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private LocalDateTime timestamp;
    private String details;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
