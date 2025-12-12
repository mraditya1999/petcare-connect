package com.petconnect.backend.services;

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
import java.util.Map;

@Service
public class UploadService {

    private final Cloudinary cloudinary;
    private final FileUtils fileUtils;
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    private final FileValidator fileValidator;

    @Autowired
    public UploadService(Cloudinary cloudinary, FileUtils fileUtils, FileValidator fileValidator) {
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
     * @throws IllegalArgumentException if the profile type is unknown
     */
    public String determineFolderPath(ProfileType profileType) {
        return switch (profileType) {
            case SPECIALIST -> "petcare_connect/specialist_profile_images";
            case USER -> "petcare_connect/user_profile_images";
            case PET -> "petcare_connect/pet_profile_images";
            default -> throw new IllegalArgumentException("Unknown profile type: " + profileType);
        };
    }

    /**
     * Uploads an image to the cloud storage.
     *
     * @param file the multipart file to be uploaded
     * @param profileType the type of profile (e.g., SPECIALIST, USER, PET)
     * @return a map containing the upload result
     * @throws IOException if an error occurs while uploading the image
     */
    public Map<String, Object> uploadImage(MultipartFile file, ProfileType profileType) throws IOException {
        logger.info("Uploading image: {}", file.getOriginalFilename());
        fileValidator.validateFile(file);
        File uploadedFile = fileUtils.convertMultipartFileToFile(file);
        Map<String, Object> uploadResult = null;
        try {
            String folderPath = determineFolderPath(profileType);
            uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.asMap(
                    "folder", folderPath
            ));
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw e;
        } finally {
            if (uploadedFile.exists() && !uploadedFile.delete()) {
                logger.warn("Failed to delete temporary file: {}", uploadedFile.getAbsolutePath());
            }
        }
        return uploadResult;
    }

    /**
     * Updates an image in the cloud storage.
     *
     * @param publicId the public ID of the existing image to be updated
     * @param file the multipart file to be uploaded
     * @param profileType the type of profile (e.g., SPECIALIST, USER, PET)
     * @return a map containing the upload result
     * @throws IOException if an error occurs while updating the image
     */
    public Map<String, Object> updateImage(String publicId, MultipartFile file, ProfileType profileType) throws IOException {
        logger.info("Updating image with publicId: {}", publicId);
        fileValidator.validateFile(file);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            logger.error("Error deleting old image: {}", e.getMessage());
            throw e;
        }
        return uploadImageWithPublicId(file, publicId, profileType);
    }

    /**
     * Deletes an image from the cloud storage.
     *
     * @param publicId the public ID of the image to be deleted
     * @return a map containing the delete result
     * @throws IOException if an error occurs while deleting the image
     */
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            logger.error("Error deleting image: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Fetches an image from the cloud storage.
     *
     * @param publicId the public ID of the image to be fetched
     * @return a map containing the image resource
     * @throws Exception if an error occurs while fetching the image
     */
    public Map<String, Object> getImage(String publicId) throws Exception {
        logger.info("Fetching image with publicId: {}", publicId);
        return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Uploads an image to the cloud storage with a specific public ID.
     *
     * @param file the multipart file to be uploaded
     * @param publicId the public ID to assign to the uploaded image
     * @param profileType the type of profile (e.g., SPECIALIST, USER, PET)
     * @return a map containing the upload result
     * @throws IOException if an error occurs while uploading the image
     */
    private Map<String, Object> uploadImageWithPublicId(MultipartFile file, String publicId, ProfileType profileType) throws IOException {
        logger.info("Uploading image with publicId: {}", publicId);
        fileValidator.validateFile(file);
        File uploadedFile = fileUtils.convertMultipartFileToFile(file);
        Map<String, Object> uploadResult = null;
        try {
            String folderPath = determineFolderPath(profileType);
            uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folderPath
            ));
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw e;
        } finally {
            if (uploadedFile.exists() && !uploadedFile.delete()) {
                logger.warn("Failed to delete temporary file: {}", uploadedFile.getAbsolutePath());
            }
        }
        return uploadResult;
    }
}
