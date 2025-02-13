package com.petconnect.backend.exceptions;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause); // Include the cause for better logging
    }
}
