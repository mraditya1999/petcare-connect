package com.petconnect.backend.utils;

import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.services.UploadService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Component
public class ProfileImageService {

    private final UploadService uploadService;
    private final FileUtils fileUtils;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProfileImageService.class);


    public ProfileImageService(UploadService uploadService, FileUtils fileUtils) {
        this.uploadService = uploadService;
        this.fileUtils = fileUtils;
    }

    public void handleProfileImageUpload(MultipartFile profileImage, User user) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = uploadProfileImage(profileImage, UploadService.ProfileType.USER);
            user.setAvatarUrl(imageInfo.get("avatarUrl"));
            user.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }
    }

    public void handleProfileImageUpload(MultipartFile profileImage, Specialist specialist) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = uploadProfileImage(profileImage, UploadService.ProfileType.SPECIALIST);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }
    }

    public void handleProfileImageUpload(MultipartFile profileImage, Pet pet) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = uploadProfileImage(profileImage, UploadService.ProfileType.PET);
            pet.setAvatarUrl(imageInfo.get("avatarUrl"));
            pet.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }
    }
    public void updateProfileImage(MultipartFile profileImage, String existingPublicId, UploadService.ProfileType profileType) throws IOException {
        fileUtils.validateFile(profileImage);
        uploadService.updateImage(existingPublicId, profileImage, profileType);
    }

    private Map<String, String> uploadProfileImage(MultipartFile profileImage, UploadService.ProfileType profileType) throws IOException {
        fileUtils.validateFile(profileImage);
        File uploadedFile = fileUtils.convertMultipartFileToFile(profileImage);

        Map<String, Object> uploadResult = null;
        try {
            String folderPath = determineFolderPath(profileType) + "/" + UUID.randomUUID().toString(); // create a new folder for each image
            uploadResult = uploadService.uploadImage(profileImage, profileType); // Ensure correct method call// Ensure correct method call
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw e;
        } finally {
            if (uploadedFile.exists() && !uploadedFile.delete()) {
                logger.warn("Failed to delete temporary file: {}", uploadedFile.getAbsolutePath());
            }
        }

        return Map.of(
                "avatarUrl", (String) uploadResult.get("url"),
                "avatarPublicId", (String) uploadResult.get("public_id")
        );
    }

    private String determineFolderPath(UploadService.ProfileType profileType) {
        switch (profileType) {
            case USER:
                return "petcare_connect/user_profile_images";
            case SPECIALIST:
                return "petcare_connect/specialist_profile_images";
            case PET:
                return "petcare_connect/pet_profile_images";
            default:
                throw new IllegalArgumentException("Unknown profile type: " + profileType);
        }
    }
}
