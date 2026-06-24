package com.spring.petcareConnect.utils;

import com.spring.petcareConnect.validators.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private final FileValidator fileValidator;

    @Autowired
    public FileUtils(FileValidator fileValidator) {
        this.fileValidator = fileValidator;
    }

    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        Objects.requireNonNull(file, "File cannot be null");
        fileValidator.validateFile(file);

        // Define root folder (project root or "uploads")
        File uploadDir = new File("uploads"); // use "." for project root
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            logger.error("Failed to create upload directory: {}", uploadDir.getAbsolutePath());
            throw new IOException("Failed to create upload directory: " + uploadDir.getAbsolutePath());
        }

        // Use original filename
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());

        // Avoid overwriting by appending timestamp
        File destFile = new File(uploadDir, System.currentTimeMillis() + "_" + originalFilename);

        try {
            Files.write(destFile.toPath(), file.getBytes());
            logger.info("File successfully written: {}", destFile.getAbsolutePath());
            return destFile;
        } catch (IOException e) {
            logger.error("Failed to process file {}: {}", originalFilename, e.getMessage(), e);
            Files.deleteIfExists(destFile.toPath());
            throw new IOException("Failed to process file: " + e.getMessage(), e);
        }
    }
}
