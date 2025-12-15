package com.petconnect.backend.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.validators.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadService {

    private final Cloudinary cloudinary;
    private final FileUtils fileUtils;
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    private final FileValidator fileValidator;

    @Autowired
    public UploadService(Cloudinary cloudinary, FileUtils fileUtils, FileValidator fileValidator) {
        if (cloudinary == null) {
            throw new IllegalArgumentException("Cloudinary cannot be null");
        }
        if (fileUtils == null) {
            throw new IllegalArgumentException("FileUtils cannot be null");
        }
        if (fileValidator == null) {
            throw new IllegalArgumentException("FileValidator cannot be null");
        }
        this.cloudinary = cloudinary;
        this.fileUtils = fileUtils;
        this.fileValidator = fileValidator;
    }

    public enum ProfileType {
        SPECIALIST,
        USER,
        PET
    }

    /**
     * Determines the folder path based on the profile type.
     *
     * @param profileType the type of profile (e.g., SPECIALIST, USER, PET)
     * @return the folder path as a string
     * @throws IllegalArgumentException if the profile type is null or unknown
     */
    public String determineFolderPath(ProfileType profileType) {
        if (profileType == null) {
            throw new IllegalArgumentException("Profile type cannot be null");
        }
        return switch (profileType) {
            case SPECIALIST -> "petcare_connect/specialist_profile_images";
            case USER -> "petcare_connect/user_profile_images";
            case PET -> "petcare_connect/pet_profile_images";
        };
    }

    /**
     * Uploads an image to the cloud storage with retry and circuit breaker support.
     *
     * @param file the multipart file to be uploaded (must not be null)
     * @param profileType the type of profile (must not be null)
     * @return a map containing the upload result with keys: url, public_id, etc.
     * @throws IllegalArgumentException if file or profileType is null
     * @throws IOException if an error occurs while uploading the image
     */
    @CircuitBreaker(name = "uploadService", fallbackMethod = "uploadImageFallback")
    @Retryable(value = { IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public Map<String, Object> uploadImage(MultipartFile file, ProfileType profileType) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (profileType == null) {
            throw new IllegalArgumentException("Profile type cannot be null");
        }
        
        logger.info("Uploading image: {}", file.getOriginalFilename());
        fileValidator.validateFile(file);
        
        File uploadedFile = null;
        try {
            uploadedFile = fileUtils.convertMultipartFileToFile(file);
            String folderPath = determineFolderPath(profileType);
            Map<String, Object> result = cloudinary.uploader().upload(uploadedFile,
                    ObjectUtils.asMap("folder", folderPath));
            logger.info("Successfully uploaded image: {}", file.getOriginalFilename());
            return result;
        } catch (IOException e) {
            logger.error("Error uploading image: {} - {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        } finally {
            cleanupTemporaryFile(uploadedFile);
        }
    }

    /**
     * Fallback method for uploadImage when circuit breaker is open or service is unavailable.
     *
     * @param file the multipart file that failed to upload
     * @param profileType the profile type
     * @param e the exception that triggered the fallback
     * @return a map with error status and message
     */
    public Map<String, Object> uploadImageFallback(MultipartFile file, ProfileType profileType, Exception e) {
        logger.error("Fallback method called for uploadImage due to: {}", e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Image upload service is currently unavailable. Please try again later.");
        response.put("error", e.getMessage());
        return response;
    }

    /**
     * Updates an image in the cloud storage by deleting the old one and uploading a new one.
     *
     * @param publicId the public ID of the existing image to be updated (must not be null or blank)
     * @param file the multipart file to be uploaded (must not be null)
     * @param profileType the type of profile (must not be null)
     * @return a map containing the upload result
     * @throws IllegalArgumentException if any parameter is null or publicId is blank
     * @throws IOException if an error occurs while updating the image
     */
    public Map<String, Object> updateImage(String publicId, MultipartFile file, ProfileType profileType) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Public ID cannot be null or blank");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (profileType == null) {
            throw new IllegalArgumentException("Profile type cannot be null");
        }
        
        logger.info("Updating image with publicId: {}", publicId);
        fileValidator.validateFile(file);
        
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.debug("Deleted old image with publicId: {}", publicId);
        } catch (Exception e) {
            logger.warn("Error deleting old image with publicId: {} - {}", publicId, e.getMessage());
            // Continue with upload even if deletion fails
        }
        
        return uploadImageWithPublicId(file, publicId, profileType);
    }

    /**
     * Deletes an image from the cloud storage.
     *
     * @param publicId the public ID of the image to be deleted (must not be null or blank)
     * @return a map containing the delete result
     * @throws IllegalArgumentException if publicId is null or blank
     * @throws IOException if an error occurs while deleting the image
     */
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Public ID cannot be null or blank");
        }
        
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Successfully deleted image with publicId: {}", publicId);
            return result;
        } catch (Exception e) {
            logger.error("Error deleting image with publicId: {} - {}", publicId, e.getMessage(), e);
            throw new IOException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches an image from the cloud storage.
     *
     * @param publicId the public ID of the image to be fetched (must not be null or blank)
     * @return a map containing the image resource
     * @throws IllegalArgumentException if publicId is null or blank
     * @throws Exception if an error occurs while fetching the image
     */
    public Map<String, Object> getImage(String publicId) throws Exception {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Public ID cannot be null or blank");
        }
        
        logger.info("Fetching image with publicId: {}", publicId);
        try {
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            logger.error("Error fetching image with publicId: {} - {}", publicId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Uploads an image to the cloud storage with a specific public ID.
     *
     * @param file the multipart file to be uploaded (must not be null)
     * @param publicId the public ID to assign to the uploaded image (must not be null or blank)
     * @param profileType the type of profile (must not be null)
     * @return a map containing the upload result
     * @throws IllegalArgumentException if any parameter is null or publicId is blank
     * @throws IOException if an error occurs while uploading the image
     */
    private Map<String, Object> uploadImageWithPublicId(MultipartFile file, String publicId, ProfileType profileType) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Public ID cannot be null or blank");
        }
        if (profileType == null) {
            throw new IllegalArgumentException("Profile type cannot be null");
        }
        
        logger.info("Uploading image with publicId: {}", publicId);
        fileValidator.validateFile(file);
        
        File uploadedFile = null;
        try {
            uploadedFile = fileUtils.convertMultipartFileToFile(file);
            String folderPath = determineFolderPath(profileType);
            Map<String, Object> result = cloudinary.uploader().upload(uploadedFile, ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folderPath
            ));
            logger.info("Successfully uploaded image with publicId: {}", publicId);
            return result;
        } catch (IOException e) {
            logger.error("Error uploading image with publicId: {} - {}", publicId, e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        } finally {
            cleanupTemporaryFile(uploadedFile);
        }
    }

    /**
     * Safely cleans up a temporary file.
     *
     * @param file the temporary file to delete (may be null)
     */
    private void cleanupTemporaryFile(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                logger.warn("Failed to delete temporary file: {}", file.getAbsolutePath());
            } else {
                logger.debug("Successfully deleted temporary file: {}", file.getAbsolutePath());
            }
        }
    }
}
