package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.profile.request.UpdatePasswordRequestDto;
import com.spring.petcareConnect.dtos.profile.request.UserProfileRequestDto;
import com.spring.petcareConnect.dtos.profile.response.UserProfileResponseDto;
import com.spring.petcareConnect.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;


public interface UserProfileService {
    UserProfileResponseDto getUserProfile();

    UserProfileResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto, MultipartFile profileImage);

    void deleteUserProfile();

    void updatePassword( @Valid UpdatePasswordRequestDto updatePasswordRequestDTO);
}
