package com.petconnect.backend.validators;

import com.petconnect.backend.exceptions.FileValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class FileValidator extends BaseValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_FORMATS = List.of(
        "image/jpeg",
        "image/png",
        "image/webp",
        "application/pdf"
    );

    /**
     * Validates the provided multipart file (size, content type and magic number).
     *
     * @param file the multipart file to be validated
     * @throws FileValidationException if the file is invalid
     */
    public void validateFile(MultipartFile file) {
        requireNotNull(file, "File");
        if (file.isEmpty()) {
            throw new FileValidationException("No file provided");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds the maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FORMATS.contains(contentType.toLowerCase())) {
            throw new FileValidationException("Invalid file format. Allowed types: JPEG, PNG, WEBP, PDF");
        }

        // Basic magic-number check to avoid simple spoofing via content-type header
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[16];
            int read = is.read(header);
            if (read <= 0 || !matchesMagicNumber(header, contentType)) {
                throw new FileValidationException("File content does not match declared content type");
            }
        } catch (IOException e) {
            throw new FileValidationException("Unable to read file for validation: " + e.getMessage());
        }

        // Placeholder: integrate with virus-scan service here if available
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
            throw new FileValidationException("Only one file is allowed.");
        }
        return files != null && !files.isEmpty() ? files.get(0) : null;
    }

    private boolean matchesMagicNumber(byte[] header, String contentType) {
        if (contentType == null || header == null) return false;
        contentType = contentType.toLowerCase();

        try {
            if (contentType.contains("jpeg") || contentType.equals("image/jpg")) {
                // JPEG starts with FF D8
                return header[0] == (byte)0xFF && header[1] == (byte)0xD8;
            }
            if (contentType.contains("png")) {
                // PNG starts with 89 50 4E 47
                return header[0] == (byte)0x89 && header[1] == (byte)0x50 && header[2] == (byte)0x4E && header[3] == (byte)0x47;
            }
            if (contentType.contains("gif")) {
                // GIF starts with 'GIF8'
                return header[0] == 'G' && header[1] == 'I' && header[2] == 'F' && header[3] == '8';
            }
            if (contentType.contains("webp")) {
                // WEBP is RIFF....WEBP -> header starts with 'RIFF' and contains 'WEBP' at position 8
                boolean riff = header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F';
                boolean webp = header.length > 11 && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
                return riff && webp;
            }
            if (contentType.equals("application/pdf")) {
                // PDF starts with %PDF-
                return header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F' && header[4] == '-';
            }
        } catch (IndexOutOfBoundsException ignored) {
            return false;
        }

        return false;
    }
}
