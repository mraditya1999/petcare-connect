package com.petconnect.backend.exceptions;

public class ImageDeletionException extends RuntimeException {

    public ImageDeletionException(String message) {
        super(message);
    }

    public ImageDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
