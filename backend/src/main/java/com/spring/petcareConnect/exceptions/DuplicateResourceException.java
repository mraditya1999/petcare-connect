package com.spring.petcareConnect.exceptions;

public class DuplicateResourceException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public DuplicateResourceException() {
    }

    public DuplicateResourceException(String resourceName, String field, String fieldName) {
        super(String.format("%s already exist with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public DuplicateResourceException(String resourceName, String field, Long fieldId) {
        super(String.format("%s already exist with %s : %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public DuplicateResourceException(String s) {
    }
}
