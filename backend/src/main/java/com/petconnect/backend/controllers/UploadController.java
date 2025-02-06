package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(@RequestParam("profile-image") MultipartFile profileImage) {
        logger.info("Uploading image");
        try {
            Map<String, Object> uploadResult = uploadService.uploadImage(profileImage);
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>("Image uploaded successfully", uploadResult);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateImage(@RequestParam("public_id") String publicId, @RequestParam("profile-image") MultipartFile profileImage) {
        logger.info("Updating image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> updateResult = uploadService.updateImage(decodedPublicId, profileImage);
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>("Image updated successfully", updateResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            logger.error("Error updating image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteImage(@RequestParam("public_id") String publicId) {
        logger.info("Deleting image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> deleteResult = uploadService.deleteImage(decodedPublicId);
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>("Image deleted successfully", deleteResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (IOException e) {
            logger.error("Error deleting image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getImage(@RequestParam("public_id") String publicId) {
        logger.info("Fetching image with publicId: {}", publicId);
        try {
            String decodedPublicId = URLDecoder.decode(publicId, StandardCharsets.UTF_8.name());
            Map<String, Object> getResult = uploadService.getImage(decodedPublicId);
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>("Image fetched successfully", getResult);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
