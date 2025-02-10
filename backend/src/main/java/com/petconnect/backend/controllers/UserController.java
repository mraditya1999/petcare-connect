package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.services.UploadService;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.mappers.UserMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UploadService uploadService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, UploadService uploadService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching profile for user: {}", userDetails.getUsername());
        UserDTO userDTO = userService.getUserProfile(userDetails.getUsername());
        ApiResponse<UserDTO> apiResponse = new ApiResponse<>("Profile fetched successfully", userDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("firstName") Optional<String> firstName,
            @RequestPart("lastName") Optional<String> lastName,
            @RequestPart("email") Optional<String> email,
            @RequestPart("mobileNumber") Optional<String> mobileNumber,
            @RequestPart("address.pincode") Optional<Long> pincode,
            @RequestPart("address.city") Optional<String> city,
            @RequestPart("address.state") Optional<String> state,
            @RequestPart("address.country") Optional<String> country,
            @RequestPart("address.locality") Optional<String> locality,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        System.out.println(pincode);
        System.out.println(city);
        System.out.println(state);
        try {
            User currentUser = userService.findUserByEmail(userDetails.getUsername());

            // Create UserDTO and AddressDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(currentUser.getUserId());

            AddressDTO addressDTO = new AddressDTO();

            // Update only fields that are provided
            firstName.ifPresent(userDTO::setFirstName);
            lastName.ifPresent(userDTO::setLastName);
            email.ifPresent(userDTO::setEmail);
            mobileNumber.ifPresent(userDTO::setMobileNumber);

            // Update address fields
            pincode.ifPresent(addressDTO::setPincode);
            city.ifPresent(addressDTO::setCity);
            state.ifPresent(addressDTO::setState);
            country.ifPresent(addressDTO::setCountry);
            locality.ifPresent(addressDTO::setLocality);

            // Set updated address to userDTO if any address field is provided
            if (pincode.isPresent() || city.isPresent() || state.isPresent() || country.isPresent() || locality.isPresent()) {
                userDTO.setAddress(addressDTO);
            }

            // Handle profile image
            if (profileImage != null) {
                Map<String, Object> uploadResult;
                if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
                    uploadResult = uploadService.updateImage(currentUser.getAvatarPublicId(), profileImage);
                } else {
                    uploadResult = uploadService.uploadImage(profileImage);
                }
                userDTO.setAvatarUrl((String) uploadResult.get("url"));
                userDTO.setAvatarPublicId((String) uploadResult.get("public_id"));
            }

            UserDTO updatedUserDTO = userService.updateUserProfile(currentUser, userDTO);
            ApiResponse<UserDTO> apiResponse = new ApiResponse<>("Profile updated successfully", updatedUserDTO);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<UserDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.deleteUserProfile(userDetails.getUsername());
            ApiResponse<Void> apiResponse = new ApiResponse<>("User deleted successfully", null);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.updatePassword(userDetails.getUsername(), updatePasswordRequest, userDetails);
            ApiResponse<Void> apiResponse = new ApiResponse<>("Password updated successfully", null);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


//    @GetMapping("/users/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
//        logger.info("Fetching all users");
//        List<UserDTO> users = userService.getAllUsers().stream()
//                .map(userMapper::toDTO)
//                .collect(Collectors.toList());
//        ApiResponse<List<UserDTO>> apiResponse = new ApiResponse<>("Users fetched successfully", users);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @GetMapping("/users/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
//        User user = userService.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
//        UserDTO userDTO = userMapper.toDTO(user);
//        ApiResponse<UserDTO> apiResponse = new ApiResponse<>("User fetched successfully", userDTO);
//        return ResponseEntity.ok(apiResponse);
//    }
}