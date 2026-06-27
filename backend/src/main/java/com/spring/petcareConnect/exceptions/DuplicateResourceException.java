package com.spring.petcareConnect.exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String entity, String field, Object value) {
        super(String.format("%s with %s '%s' already exist", entity, field, value));
    }
}
