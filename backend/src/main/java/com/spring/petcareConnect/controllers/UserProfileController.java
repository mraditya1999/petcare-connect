package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.profile.request.UpdatePasswordRequestDto;
import com.spring.petcareConnect.dtos.profile.request.UserProfileRequestDto;
import com.spring.petcareConnect.dtos.profile.response.UserProfileResponseDto;
import com.spring.petcareConnect.security.service.UserDetailsImpl;
import com.spring.petcareConnect.services.UserProfileService;
import com.spring.petcareConnect.config.ResponseMessages;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<UserProfileResponseDto>> getUserProfile() {
        UserProfileResponseDto responseDto = userProfileService.getUserProfile();
        CustomApiResponse<UserProfileResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PROFILE_FETCHED, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse<UserProfileResponseDto>> updateUserProfile(
            @Valid @ModelAttribute UserProfileRequestDto userProfileRequestDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        UserProfileResponseDto responseDto = userProfileService.updateUserProfile(userProfileRequestDto, profileImage);
        CustomApiResponse<UserProfileResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PROFILE_UPDATED, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<CustomApiResponse<Void>> deleteUserProfile() {
        userProfileService.deleteUserProfile();
        CustomApiResponse<Void> response = new CustomApiResponse<>(true, ResponseMessages.PROFILE_DELETED, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<CustomApiResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDTO) {
        userProfileService.updatePassword( updatePasswordRequestDTO);
        CustomApiResponse<Void> response = new CustomApiResponse<>(true, ResponseMessages.PASSWORD_UPDATED_PREFIX, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
