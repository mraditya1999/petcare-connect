package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.AddressMapper;
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
    private final AddressMapper addressMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, UploadService uploadService,AddressMapper addressMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
        this.addressMapper = addressMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching profile for user: {}", userDetails.getUsername());
        UserDTO userDTO = userService.getUserProfile(userDetails.getUsername());
        ApiResponse<UserDTO> apiResponse = new ApiResponse<>("Profile fetched successfully", userDTO);
        return ResponseEntity.ok(apiResponse);
    }

//    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("email") String email,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("pincode") Optional<Long> pincode,
//            @RequestParam("city") String city,
//            @RequestParam("state") String state,
//            @RequestParam("country") String country,
//            @RequestParam("locality") String locality,
//            @RequestParam("profileImage") MultipartFile profileImage
//    ) {
//        try {
//            User currentUser = userService.findUserByEmail(userDetails.getUsername());
//
//            // Create UserDTO and AddressDTO
//            UserDTO userDTO = new UserDTO();
//            userDTO.setUserId(currentUser.getUserId());
//
//            AddressDTO addressDTO = new AddressDTO();
//
//            // Update only fields that are provided
//            firstName.ifPresent(userDTO::setFirstName);
//            lastName.ifPresent(userDTO::setLastName);
//            email.ifPresent(userDTO::setEmail);
//            mobileNumber.ifPresent(userDTO::setMobileNumber);
//
//            // Update address fields
//            pincode.ifPresent(addressDTO::setPincode);
//            city.ifPresent(addressDTO::setCity);
//            state.ifPresent(addressDTO::setState);
//            country.ifPresent(addressDTO::setCountry);
//            locality.ifPresent(addressDTO::setLocality);
//
//            // Set updated address to userDTO if any address field is provided
//            if (pincode.isPresent() || city.isPresent() || state.isPresent() || country.isPresent() || locality.isPresent()) {
//                userDTO.setAddress(addressDTO);
//            }
//
//            // Handle profile image
//            if (profileImage != null) {
//                Map<String, Object> uploadResult;
//                if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
//                    uploadResult = uploadService.updateImage(currentUser.getAvatarPublicId(), profileImage);
//                } else {
//                    uploadResult = uploadService.uploadImage(profileImage);
//                }
//                userDTO.setAvatarUrl((String) uploadResult.get("url"));
//                userDTO.setAvatarPublicId((String) uploadResult.get("public_id"));
//            }
//
//            UserDTO updatedUserDTO = userService.updateUserProfile(currentUser, userDTO);
//            ApiResponse<UserDTO> apiResponse = new ApiResponse<>("Profile updated successfully", updatedUserDTO);
//            return ResponseEntity.ok(apiResponse);
//        } catch (ResourceNotFoundException e) {
//            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (IllegalArgumentException e) {
//            ApiResponse<UserDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            ApiResponse<UserDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "pincode", required = false) Long pincode,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "locality", required = false) String locality,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        logger.info("Received request to update user profile for: {}", userDetails.getUsername());

        try {
            User currentUser = userService.findUserByEmail(userDetails.getUsername());
            logger.info("Found user: {}", currentUser.getUserId());

            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(currentUser.getUserId());

            if (firstName != null) userDTO.setFirstName(firstName);
            if (lastName != null) userDTO.setLastName(lastName);
            if (email != null) userDTO.setEmail(email);
            if (mobileNumber != null) userDTO.setMobileNumber(mobileNumber);

            // Handle Address update
            AddressDTO addressDTO = (currentUser.getAddress() != null) ?
                    addressMapper.toDTO(currentUser.getAddress()) : new AddressDTO();

            if (pincode != null) addressDTO.setPincode(pincode);
            if (city != null) addressDTO.setCity(city);
            if (state != null) addressDTO.setState(state);
            if (country != null) addressDTO.setCountry(country);
            if (locality != null) addressDTO.setLocality(locality);

            if (pincode != null || city != null || state != null || country != null || locality != null) {
                userDTO.setAddress(addressDTO);
            }

            // Handle profile image upload
            if (profileImage != null) {
                logger.info("Uploading new profile image for user: {}", currentUser.getUserId());
                Map<String, Object> uploadResult;
                if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
                    uploadResult = uploadService.updateImage(currentUser.getAvatarPublicId(), profileImage);
                } else {
                    uploadResult = uploadService.uploadImage(profileImage);
                }
                userDTO.setAvatarUrl((String) uploadResult.get("url"));
                userDTO.setAvatarPublicId((String) uploadResult.get("public_id"));
            }

            // Update user
            UserDTO updatedUserDTO = userService.updateUserProfile(currentUser, userDTO);
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