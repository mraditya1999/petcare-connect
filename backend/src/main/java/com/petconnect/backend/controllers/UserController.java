package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.user.UpdatePasswordRequestDTO;
import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.user.UserUpdateDTO;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import com.petconnect.backend.validators.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserController extends BaseController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final FileValidator fileValidator;

    public UserController(UserService userService, FileValidator fileValidator) {
        super(logger);
        this.userService = userService;
        this.fileValidator = fileValidator;
    }

    /**
     * Fetches the user profile.
     *
     * @param userDetails the authenticated user's details
     * @return the response entity containing the user profile
     */
    @Operation(
            summary = "Get user profile",
            description = "Fetches the profile details of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
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
    @Operation(
            summary = "Update user profile",
            description = "Updates user profile details and optionally uploads a profile image"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateUserProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid
            @ModelAttribute UserUpdateDTO userUpdateDTO,
            @Parameter(
                    description = "Profile image file (optional)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam(value = "profileImage", required = false)
            List<MultipartFile> profileImages
    ) throws IOException {

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
    @Operation(
            summary = "Delete user profile",
            description = "Deletes the authenticated user's profile permanently"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<String>> deleteUserProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
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
    @Operation(
            summary = "Update password",
            description = "Updates the authenticated user's password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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
