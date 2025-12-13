package com.petconnect.backend.exceptions;

public class FileValidationException extends ApiException {
    public FileValidationException(String message) {
        super(message);
    }
}
