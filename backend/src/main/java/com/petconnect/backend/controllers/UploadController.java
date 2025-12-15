package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.services.UploadService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final UploadService uploadService;
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/{profileType}")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> uploadImage(
            @PathVariable("profileType") UploadService.ProfileType profileType,
            @RequestParam("profile-image") MultipartFile profileImage) {
        logger.info("Uploading image for profile type: {}", profileType);
        try {
            Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, profileType);
            return ResponseEntityUtil.created("Image uploaded successfully", uploadResult);
        } catch (IllegalArgumentException e) {
            logger.error("Error uploading image", e);
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (IOException e) {
            logger.error("Error uploading image", e);
            return ResponseEntityUtil.internalServerError("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{profileType}")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> updateImage(
            @PathVariable("profileType") UploadService.ProfileType profileType,
            @RequestParam("public_id") String publicId,
            @RequestParam("profile-image") MultipartFile profileImage) {
        logger.info("Updating image with publicId: {} for profile type: {}", publicId, profileType);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> updateResult = uploadService.updateImage(decodedPublicId, profileImage, profileType);
            return ResponseEntityUtil.ok("Image updated successfully", updateResult);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating image", e);
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (IOException e) {
            logger.error("Error updating image", e);
            return ResponseEntityUtil.internalServerError("An error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> deleteImage(@RequestParam("public_id") String publicId) {
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> deleteResult = uploadService.deleteImage(decodedPublicId);
            return ResponseEntityUtil.ok("Image deleted successfully", deleteResult);
        } catch (IOException e) {
            logger.error("Error deleting image: {}", e.getMessage());
            return ResponseEntityUtil.internalServerError("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getImage(@RequestParam("public_id") String publicId) {
        logger.info("Fetching image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> getResult = uploadService.getImage(decodedPublicId);
            return ResponseEntityUtil.ok("Image fetched successfully", getResult);
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage());
            return ResponseEntityUtil.internalServerError("An error occurred: " + e.getMessage());
        }
    }
}
