package com.petconnect.backend.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private LocalDateTime timestamp;
    private String details;
    private int statusCode;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String details, int statusCode) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
        this.statusCode = statusCode;
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private String details;
        private int statusCode;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this.message, this.details, this.statusCode);
        }
    }
}
