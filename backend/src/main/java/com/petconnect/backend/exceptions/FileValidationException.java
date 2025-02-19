package com.petconnect.backend.exceptions;

public class FileValidationException extends RuntimeException {
    public FileValidationException(String message) {
        super(message);
    }
}
