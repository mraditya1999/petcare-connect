package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.services.UploadService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Upload an image",
            description = "Upload a new image for the specified profile type",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Image uploaded successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or file"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/{profileType}")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> uploadImage(
            @Parameter(description = "Profile type for the image (e.g., USER, PET, SPECIALIST)") @PathVariable("profileType") UploadService.ProfileType profileType,
            @Parameter(description = "Image file to upload") @RequestParam("profile-image") MultipartFile profileImage) {
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

    @Operation(
            summary = "Update an existing image",
            description = "Update an existing image using publicId for the specified profile type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image updated successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or file"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/{profileType}")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> updateImage(
            @Parameter(description = "Profile type for the image") @PathVariable("profileType") UploadService.ProfileType profileType,
            @Parameter(description = "Public ID of the image to update") @RequestParam("public_id") String publicId,
            @Parameter(description = "New image file") @RequestParam("profile-image") MultipartFile profileImage) {
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

    @Operation(
            summary = "Delete an image",
            description = "Delete an image using its public ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image deleted successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> deleteImage(
            @Parameter(description = "Public ID of the image to delete") @RequestParam("public_id") String publicId) {
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

    @Operation(
            summary = "Get image details",
            description = "Fetch image details using its public ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getImage(
            @Parameter(description = "Public ID of the image to fetch") @RequestParam("public_id") String publicId) {
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
