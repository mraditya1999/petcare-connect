package com.petconnect.backend.utils;

import com.petconnect.backend.validators.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class FileUtils {

    @Autowired
    private FileValidator fileValidator;

    /**
     * Converts the provided multipart file to a File object.
     * Validation is delegated to {@link FileValidator}.
     *
     * @param file the multipart file to be converted
     * @return the converted File object
     * @throws IOException if an error occurs while converting the file
     */
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        fileValidator.validateFile(file);
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
