package com.spring.petcareConnect.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.spring.petcareConnect.dtos.upload.response.ImageDataResponseDto;
import com.spring.petcareConnect.dtos.upload.response.ImageDeleteResponseDto;
import com.spring.petcareConnect.dtos.upload.response.ImageUploadResponseDto;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.services.UploadImageService;
import com.spring.petcareConnect.utils.FileUtils;
import com.spring.petcareConnect.validators.FileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UploadImageServiceImpl implements UploadImageService {

    private static final Logger logger = LoggerFactory.getLogger(UploadImageServiceImpl.class);

    private final Cloudinary cloudinary;
    private final FileUtils fileUtils;
    private final FileValidator fileValidator;

    public UploadImageServiceImpl(Cloudinary cloudinary, FileUtils fileUtils, FileValidator fileValidator) {
        this.cloudinary = cloudinary;
        this.fileUtils = fileUtils;
        this.fileValidator = fileValidator;
    }

    @Override
    public String determineFolderPath(ProfileType profileType) {
        String folderPath = switch (profileType) {
            case SPECIALIST -> "petcare_connect/specialist_profile_images";
            case USER -> "petcare_connect/user_profile_images";
            case PET -> "petcare_connect/pet_profile_images";
        };
        logger.debug("Determined folder path for {}: {}", profileType, folderPath);
        return folderPath;
    }

    @Override
    public ImageUploadResponseDto uploadProfileImage(MultipartFile file, ProfileType profileType, String publicId) {
        logger.info("Uploading profile image for profileType={} publicId={}", profileType, publicId);

        fileValidator.validateFile(file);

        File uploadedFile = null;
        try {
            uploadedFile = fileUtils.convertMultipartFileToFile(file);
            String folderPath = determineFolderPath(profileType);

            Map<String, Object> options = ObjectUtils.asMap("folder", folderPath);
            if (publicId != null && !publicId.isBlank()) {
                options.put("public_id", publicId);
            }

            Map<String, Object> rawResult = cloudinary.uploader().upload(uploadedFile, options);
            logger.debug("Cloudinary upload result: {}", rawResult);

            ImageUploadResponseDto resultDto = new ImageUploadResponseDto();
            resultDto.setPublicId((String) rawResult.get("public_id"));
            resultDto.setUrl((String) rawResult.getOrDefault("secure_url", rawResult.get("url")));
            resultDto.setFolderPath(folderPath);
            resultDto.setFormat((String) rawResult.get("format"));
            resultDto.setSize(((Number) rawResult.get("bytes")).longValue());

            logger.info("Successfully uploaded image with publicId={}", resultDto.getPublicId());
            return resultDto;

        } catch (Exception e) {
            logger.error("Failed to upload image for profileType={} publicId={}", profileType, publicId, e);
            throw new APIException("Failed to upload image: " + e.getMessage(), e);
        } finally {
            if (uploadedFile != null) {
                try {
                    Files.deleteIfExists(uploadedFile.toPath());
                    logger.debug("Temporary file deleted: {}", uploadedFile.getAbsolutePath());
                } catch (IOException cleanupEx) {
                    logger.warn("Failed to clean up temp file: {}", uploadedFile.getAbsolutePath(), cleanupEx);
                }
            }
        }
    }

    @Override
    public ImageDeleteResponseDto deleteProfileImage(String publicId) {
        logger.info("Deleting profile image with publicId={}", publicId);

        if (publicId == null || publicId.isBlank()) {
            throw new APIException("Public ID cannot be null or blank");
        }

        try {
            Map<String, Object> rawResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) rawResult.get("result");
            logger.debug("Cloudinary delete result: {}", rawResult);

            if ("ok".equalsIgnoreCase(status)) {
                logger.info("Successfully deleted image with publicId={}", publicId);
                return new ImageDeleteResponseDto(publicId, true, "Image deleted successfully");
            } else {
                throw new APIException("Image not found or could not be deleted");
            }

        } catch (Exception e) {
            logger.error("Failed to delete image with publicId={}", publicId, e);
            throw new APIException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    @Override
    public ImageDataResponseDto getImage(String publicId) {
        logger.info("Fetching image details for publicId={}", publicId);

        if (publicId == null || publicId.isBlank()) {
            throw new APIException("Public ID cannot be null or blank");
        }

        try {
            Map<String, Object> rawResult = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            logger.debug("Cloudinary get resource result: {}", rawResult);

            ImageDataResponseDto imageData = new ImageDataResponseDto();
            imageData.setPublicId((String) rawResult.get("public_id"));
            imageData.setUrl((String) rawResult.get("secure_url"));
            imageData.setFolderPath((String) rawResult.get("folder"));
            imageData.setFormat((String) rawResult.get("format"));
            imageData.setWidth(((Number) rawResult.get("width")).intValue());
            imageData.setHeight(((Number) rawResult.get("height")).intValue());

            logger.info("Successfully fetched image data for publicId={}", publicId);
            return imageData;

        } catch (Exception e) {
            logger.error("Failed to fetch image details for publicId={}", publicId, e);
            throw new APIException("Failed to fetch image: " + e.getMessage(), e);
        }
    }
}
