//package com.petconnect.backend.utils;
//
//import com.petconnect.backend.exceptions.FileValidationException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//import java.util.Objects;
//
//@Component
//public class FileUtils {
//
//    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
//    private static final List<String> ALLOWED_FORMATS = List.of("image/jpeg", "image/png", "image/gif");
//
//    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
//        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
//        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//            fos.write(file.getBytes());
//        }
//        return convertedFile;
//    }
//
//    public void validateFile(MultipartFile file) {
//        if (file.getSize() > MAX_FILE_SIZE) {
//            throw new FileValidationException("File size exceeds the maximum limit of 5MB");
//        }
//        if (file.getContentType() == null || !ALLOWED_FORMATS.contains(file.getContentType().toLowerCase())) {
//            throw new FileValidationException("Invalid file format. Only JPEG, PNG, and GIF are allowed");
//        }
//    }
//
//    public MultipartFile getSingleFile(List<MultipartFile> files) {
//        if (files != null && files.size() > 1) {
//            throw new FileValidationException("Only one image file is allowed.");
//        }
//        return files != null && !files.isEmpty() ? files.getFirst() : null;
//    }
//}

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
