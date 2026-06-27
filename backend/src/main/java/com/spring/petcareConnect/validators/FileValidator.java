package com.spring.petcareConnect.validators;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.exceptions.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FileValidator {

    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    public void validateFile(MultipartFile file) {
        if (file == null) {
            logger.error("File validation failed: file is null");
            throw new ValidationException("File cannot be null");
        }

        if (file.isEmpty()) {
            logger.error("File validation failed: no file provided");
            throw new ValidationException("No file provided");
        }

        if (file.getSize() > AppConstants.MAX_FILE_SIZE) {
            logger.error("File validation failed: size={} exceeds max limit of {} bytes", file.getSize(), AppConstants.MAX_FILE_SIZE);
            throw new ValidationException("File size exceeds the maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !AppConstants.ALLOWED_FILE_FORMATS.contains(contentType.toLowerCase())) {
            logger.error("File validation failed: invalid format={} allowed={}", contentType, AppConstants.ALLOWED_FILE_FORMATS);
            throw new ValidationException("Invalid file format. Allowed types: JPEG, PNG, WEBP, PDF");
        }

        logger.info("File validation passed: name={} size={} type={}", file.getOriginalFilename(), file.getSize(), contentType);
    }
}
