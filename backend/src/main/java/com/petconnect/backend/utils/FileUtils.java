package com.petconnect.backend.utils;

import com.petconnect.backend.validators.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Component
public class FileUtils {

    private static final String TEMP_DIR_PREFIX = "petcare_upload_";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private final FileValidator fileValidator;

    @Autowired
    public FileUtils(FileValidator fileValidator) {
        this.fileValidator = fileValidator;
    }

    /**
     * Converts the provided multipart file to a File object.
     * Validation is delegated to {@link FileValidator}.
     * Files are created in the system temp directory and marked for deletion on exit.
     *
     * @param file the multipart file to be converted
     * @return the converted File object in temp directory
     * @throws IOException if an error occurs while converting the file
     */
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        fileValidator.validateFile(file);
        
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        File tempFile = File.createTempFile(TEMP_DIR_PREFIX, "_" + originalFilename, new File(TEMP_DIR));
        
        // Mark for deletion on JVM exit as a safety measure
        tempFile.deleteOnExit();
        
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            // Clean up on error
            if (tempFile.exists()) {
                Files.deleteIfExists(tempFile.toPath());
            }
            throw new IOException("Failed to write file to temp directory: " + e.getMessage(), e);
        }
        
        return tempFile;
    }
}
