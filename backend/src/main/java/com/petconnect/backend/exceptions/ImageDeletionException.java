package com.petconnect.backend.exceptions;

public class ImageDeletionException extends ApiException {

    public ImageDeletionException(String message) {
        super(message);
    }

    public ImageDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
