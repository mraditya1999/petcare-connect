package com.spring.petcareConnect.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String field;
    private String fieldValue;
    private Long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldValue) {
        super(String.format("%s not found with %s : %s", resourceName, field, fieldValue));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s : %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public static ResourceNotFoundException byId(String resourceName, Long id) {
        return new ResourceNotFoundException(resourceName, "id", id);
    }

    public static ResourceNotFoundException byField(String resourceName, String field, String value) {
        return new ResourceNotFoundException(resourceName, field, value);
    }

}
