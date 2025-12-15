package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.user.UpdatePasswordRequestDTO;
import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.user.UserUpdateDTO;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import com.petconnect.backend.validators.FileValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profile")
@Validated
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final FileUtils fileUtils;
    private final FileValidator fileValidator;

    @Autowired
    public UserController(UserService userService, FileUtils fileUtils, FileValidator fileValidator) {
        this.userService = userService;
        this.fileUtils = fileUtils;
        this.fileValidator = fileValidator;
    }

    /**
     * Fetches the user profile.
     *
     * @param userDetails the authenticated user's details
     * @return the response entity containing the user profile
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching profile for user: {}", userDetails.getUsername());
        UserDTO userProfile = userService.getUserProfile(userDetails.getUsername());
        logger.info("Profile fetched successfully for user: {}", userDetails.getUsername());
        return ResponseEntityUtil.ok("Profile fetched successfully", userProfile);
    }

    /**
     * Handles updating the user profile.
     *
     * @param userDetails the authenticated user's details
     * @param userUpdateDTO the data transfer object containing user update information
     * @param profileImages the list of uploaded profile images
     * @return the ResponseEntity containing the ApiResponseDTO with the updated user information
     */
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UserUpdateDTO userUpdateDTO,
            @RequestParam(value = "profileImage", required = false) List<MultipartFile> profileImages) throws IOException {

        logger.info("Received request to update user profile for: {}", userDetails.getUsername());
        MultipartFile profileImage = fileValidator.getSingleFile(profileImages);
        if (profileImage != null) {
            fileValidator.validateFile(profileImage);
        }

        String username = userDetails.getUsername();
        UserDTO updatedUserDTO = userService.updateUserProfile(username, userUpdateDTO, profileImage);
        logger.info("User profile updated successfully: {}", updatedUserDTO);
        return ResponseEntityUtil.ok("Profile updated successfully", updatedUserDTO);
    }



    /**
     * Deletes the user profile.
     *
     * @param userDetails the authenticated user's details
     * @return the response entity indicating the deletion status
     */
    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<String>> deleteUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received request to delete profile for user: {}", userDetails.getUsername());
        userService.deleteUserProfile(userDetails);
        String successMessage = "Profile deleted successfully for user: " + userDetails.getUsername();
        logger.info(successMessage);
        return ResponseEntityUtil.ok("Profile deleted successfully", successMessage);
    }

    /**
     * Updates the user password.
     *
     * @param updatePasswordRequestDTO the request containing the new password details
     * @param userDetails the authenticated user's details
     * @return the response entity indicating the password update status
     */
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponseDTO<String>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.updatePassword(userDetails.getUsername(), updatePasswordRequestDTO, userDetails);
        String successMessage = "Password has been updated successfully for user:  " + userDetails.getUsername();
        logger.info(successMessage);
        return ResponseEntityUtil.ok("Password updated successfully", successMessage);
    }
}
