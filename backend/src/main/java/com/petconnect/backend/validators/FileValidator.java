package com.petconnect.backend.validators;

import com.petconnect.backend.exceptions.FileValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_FORMATS = List.of("image/jpeg", "image/png", "image/gif");

    /**
     * Validates the provided multipart file.
     *
     * @param file the multipart file to be validated
     * @throws FileValidationException if the file is invalid
     */
    public void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds the maximum limit of 5MB");
        }
        if (file.getContentType() == null || !ALLOWED_FORMATS.contains(file.getContentType().toLowerCase())) {
            throw new FileValidationException("Invalid file format. Only JPEG, PNG, and GIF are allowed");
        }
    }

    /**
     * Retrieves a single file from the provided list of multipart files.
     *
     * @param files the list of multipart files
     * @return the single multipart file
     * @throws FileValidationException if more than one file is provided
     */
    public MultipartFile getSingleFile(List<MultipartFile> files) {
        if (files != null && files.size() > 1) {
            throw new FileValidationException("Only one image file is allowed.");
        }
        return files != null && !files.isEmpty() ? files.get(0) : null;
    }
}
