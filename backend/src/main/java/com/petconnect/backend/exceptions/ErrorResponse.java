package com.petconnect.backend.exceptions;

import com.petconnect.backend.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ErrorResponse {

    private String message;
    private LocalDateTime timestamp;
    private String details;
    private int statusCode;

    public ErrorResponse() {
        this.timestamp = DateTimeUtils.nowUtc();
    }

    public ErrorResponse(String message, String details, int statusCode) {
        this.message = message;
        this.timestamp = DateTimeUtils.nowUtc();
        this.details = details;
        this.statusCode = statusCode;
    }

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
