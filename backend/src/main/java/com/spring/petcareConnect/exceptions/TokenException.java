package com.spring.petcareConnect.exceptions;

import lombok.Data;

@Data
public class TokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String tokenType;   // e.g. "JWT", "Verification"
    private final String reason;      // e.g. "Expired", "Invalid"

    public TokenException(String tokenType, String reason) {
        super(String.format("%s token %s", tokenType, reason.toLowerCase()));
        this.tokenType = tokenType;
        this.reason = reason;
    }

}
