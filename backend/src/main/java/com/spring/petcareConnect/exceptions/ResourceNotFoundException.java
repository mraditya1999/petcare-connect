package com.spring.petcareConnect.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, String field, Object value) {
        super(String.format("%s error: not found with %s: %s", entity, field, value));
    }

    public static ResourceNotFoundException byId(String entity, Object id) {
        return new ResourceNotFoundException(entity, "id", id);
    }

    public static ResourceNotFoundException byField(String entity, String field, Object value) {
        return new ResourceNotFoundException(entity, field, value);
    }
}
