package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@Validated
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches the user profile.
     *
     * @param userDetails the authenticated user's details
     * @return the response entity containing the user profile
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching profile for user: {}", userDetails.getUsername());
        Object userProfile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>("Profile fetched successfully", userProfile));
    }

    /**
     * Updates the user profile.
     *
     * @param userDetails the authenticated user's details
     * @param userUpdateDTO the user profile update data
     * @param profileImage the user's profile image
     * @return the response entity containing the updated user profile
     */
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UserUpdateDTO userUpdateDTO,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        logger.info("Received request to update user profile for: {}", userDetails.getUsername());

        try {
            UserDTO updatedUserDTO = userService.updateUserProfile(userDetails.getUsername(), userUpdateDTO, profileImage);
            logger.info("User profile updated successfully: {}", updatedUserDTO);
            return ResponseEntity.ok(new ApiResponse<>("Profile updated successfully", updatedUserDTO));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("An error occurred: " + e.getMessage(), null));
        }
    }

    /**
     * Deletes the user profile.
     *
     * @param userDetails the authenticated user's details
     * @return the response entity indicating the deletion status
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received request to delete profile for user: {}", userDetails.getUsername());

        try {
            userService.deleteUserProfile(userDetails);
            logger.info("User profile deleted successfully for user: {}", userDetails.getUsername());
            ApiResponse<Void> apiResponse = new ApiResponse<>("Profile deleted successfully", null);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("An error occurred: " + e.getMessage(), null));
        }
    }

    /**
     * Updates the user password.
     *
     * @param updatePasswordRequest the request containing the new password details
     * @param userDetails the authenticated user's details
     * @return the response entity indicating the password update status
     */
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            userService.updatePassword(userDetails.getUsername(), updatePasswordRequest, userDetails);
            return ResponseEntity.ok(new ApiResponse<>("Password updated successfully", null));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("An error occurred: " + e.getMessage(), null));
        }
    }
}
