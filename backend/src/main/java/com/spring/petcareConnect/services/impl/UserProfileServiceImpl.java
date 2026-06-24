package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.profile.request.AddressDto;
import com.spring.petcareConnect.dtos.profile.request.UpdatePasswordRequestDto;
import com.spring.petcareConnect.dtos.profile.request.UserProfileRequestDto;
import com.spring.petcareConnect.dtos.profile.response.UserProfileResponseDto;
import com.spring.petcareConnect.entities.Address;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.exceptions.DuplicateResourceException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.exceptions.ValidationException;
import com.spring.petcareConnect.helpers.ProfileImageHandler;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.services.UploadImageService;
import com.spring.petcareConnect.services.UserProfileService;
import com.spring.petcareConnect.utils.AuthUtils;
import com.spring.petcareConnect.utils.PhoneUtils;
import com.spring.petcareConnect.validators.FileValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileValidator fileValidator;
    private final UploadImageService uploadImageService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageHandler profileImageHandler;

    public UserProfileServiceImpl(UserRepository userRepository,
                                  ModelMapper modelMapper,
                                  FileValidator fileValidator,
                                  UploadImageService uploadImageService,
                                  PasswordEncoder passwordEncoder, ProfileImageHandler profileImageHandler) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.fileValidator = fileValidator;
        this.uploadImageService = uploadImageService;
        this.passwordEncoder = passwordEncoder;
        this.profileImageHandler = profileImageHandler;
    }

    @Override
    public UserProfileResponseDto getUserProfile() {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        return modelMapper.map(user, UserProfileResponseDto.class);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserProfile(UserProfileRequestDto dto, MultipartFile profileImage) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);

        if (profileImage != null && !profileImage.isEmpty()) {
            fileValidator.validateFile(profileImage);
        }

        applyProfileUpdates(user, dto);
        if (profileImage == null || profileImage.isEmpty()) {
            profileImageHandler.delete(user);
        } else {
            profileImageHandler.replace(user, profileImage);
        }

        userRepository.save(user);
        return modelMapper.map(user, UserProfileResponseDto.class);
    }

    @Override
    public void deleteUserProfile() {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);
        userRepository.delete(user);
        logger.info("User profile deleted successfully for email={}", email);
    }

    @Override
    public void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDTO) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new IllegalStateException("No logged-in user"));
        User user = getUserByEmailOrThrow(email);

        validatePasswordChange(user, updatePasswordRequestDTO);

        user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.getNewPassword()));
        userRepository.save(user);
        logger.info("Password updated successfully for user={}", email);
    }

    // ----------------- Helper Methods -----------------

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }

    private void validatePasswordChange(User user, UpdatePasswordRequestDto dto) {
        boolean isOAuthUser = !user.getOauthAccounts().isEmpty();
        boolean hasStoredPassword = user.getPassword() != null && !user.getPassword().isBlank();
        boolean currentPasswordProvided = dto.getCurrentPassword() != null && !dto.getCurrentPassword().isBlank();

        if (currentPasswordProvided) {
            if (!hasStoredPassword) throw new ValidationException("You do not have a current password to verify.");
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) throw new ValidationException("Current password is incorrect.");
            if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) throw new ValidationException("New password cannot be the same as the old password.");
        } else {
            if (hasStoredPassword && !isOAuthUser) {
                throw new ValidationException("Current password is required to update your password.");
            }
        }
    }

    private void applyProfileUpdates(User user, UserProfileRequestDto dto) {
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());

        if (dto.getMobileNumber() != null) {
            String normalizedPhone = PhoneUtils.normalizeToIndianFormat(dto.getMobileNumber());
            validateUniquePhone(user, normalizedPhone);
            user.setMobileNumber(normalizedPhone);
        }

        if (dto.getAddressDto() != null) {
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            updateAddressFields(address, dto.getAddressDto());
            address.setUser(user);
            user.setAddress(address);
        }
    }

    private void validateUniquePhone(User user, String phone) {
        Optional<User> existingUser = userRepository.findByMobileNumber(phone);
        if (existingUser.isPresent() && !existingUser.get().getUserId().equals(user.getUserId())) {
            throw new DuplicateResourceException("Number", "phone number", phone);
        }
        if (!PhoneUtils.isValidIndianMobile(phone)) {
            throw new ValidationException("Invalid phone number format");
        }
    }

    private void updateAddressFields(Address address, AddressDto dto) {
        if (dto.getPincode() != null) {
            address.setPincode(dto.getPincode());
        }
        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            address.setCity(dto.getCity().trim());
        }
        if (dto.getState() != null && !dto.getState().isBlank()) {
            address.setState(dto.getState().trim());
        }
        if (dto.getCountry() != null && !dto.getCountry().isBlank()) {
            address.setCountry(dto.getCountry().trim());
        }
    }

}
