package com.petconnect.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.petconnect.backend.utils.FileUtils;
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

    @Autowired
    public UploadService(Cloudinary cloudinary, FileUtils fileUtils) {
        this.cloudinary = cloudinary;
        this.fileUtils = fileUtils;
    }

    public enum ProfileType {
        SPECIALIST,
        USER,
        PET
    }

    public String determineFolderPath(ProfileType profileType) {
        switch (profileType) {
            case SPECIALIST:
                return "petcare_connect/specialist_profile_images";
            case USER:
                return "petcare_connect/user_profile_images";
            case PET:
                return "petcare_connect/pet_profile_images";
            default:
                throw new IllegalArgumentException("Unknown profile type: " + profileType);
        }
    }

    public Map<String, Object> uploadImage(MultipartFile file, ProfileType profileType) throws IOException {
        logger.info("Uploading image: {}", file.getOriginalFilename());
        fileUtils.validateFile(file);
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

    public Map<String, Object> updateImage(String publicId, MultipartFile file, ProfileType profileType) throws IOException {
        logger.info("Updating image with publicId: {}", publicId);
        fileUtils.validateFile(file);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            logger.error("Error deleting old image: {}", e.getMessage());
            throw e;
        }
        return uploadImageWithPublicId(file, publicId, profileType);
    }

    public Map<String, Object> deleteImage(String publicId) throws IOException {
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            logger.error("Error deleting image: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getImage(String publicId) throws Exception {
        logger.info("Fetching image with publicId: {}", publicId);
        try {
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> uploadImageWithPublicId(MultipartFile file, String publicId, ProfileType profileType) throws IOException {
        logger.info("Uploading image with publicId: {}", publicId);
        fileUtils.validateFile(file);
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
