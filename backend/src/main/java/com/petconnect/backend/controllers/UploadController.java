package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.services.UploadService;
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
            ApiResponseDTO<Map<String, Object>> apiResponseDTO = new ApiResponseDTO<>("Image uploaded successfully", uploadResult);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            ApiResponseDTO<Map<String, Object>> apiResponseDTO = new ApiResponseDTO<>("Image updated successfully", updateResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            logger.error("Error updating image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> deleteImage(@RequestParam("public_id") String publicId) {
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> deleteResult = uploadService.deleteImage(decodedPublicId);
            ApiResponseDTO<Map<String, Object>> apiResponseDTO = new ApiResponseDTO<>("Image deleted successfully", deleteResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } catch (IOException e) {
            logger.error("Error deleting image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getImage(@RequestParam("public_id") String publicId) {
        logger.info("Fetching image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> getResult = uploadService.getImage(decodedPublicId);
            ApiResponseDTO<Map<String, Object>> apiResponseDTO = new ApiResponseDTO<>("Image fetched successfully", getResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage());
            ApiResponseDTO<Map<String, Object>> response = new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
