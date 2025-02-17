package com.petconnect.backend.services;

import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.dto.UserUpdateDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ImageUploadException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.AddressMapper;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final UserMapper userMapper;
    private final UploadService uploadService;
    private final SpecialistMapper specialistMapper;
    private final SpecialistRepository specialistRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleAssignmentUtil roleAssignmentUtil, UserMapper userMapper, UploadService uploadService, AddressRepository addressRepository, AddressMapper addressMapper, SpecialistMapper specialistMapper, SpecialistRepository specialistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
        this.specialistMapper = specialistMapper;
        this.specialistRepository = specialistRepository;
    }

    public Object getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.SPECIALIST)) {
            return specialistMapper.toSpecialistResponseDTO((Specialist) user); // More efficient check
        }

        return userMapper.toDTO(user);
    }

    public UserDTO updateUserProfile(String username, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + username));

        updateUserFields(currentUser, userUpdateDTO);
        updateAddress(currentUser, userUpdateDTO);

        handleProfileImage(currentUser, profileImage);

        userRepository.save(currentUser);
        return userMapper.toDTO(currentUser);
    }

    public void deleteUserProfile(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        deleteAvatar(user); // Helper method to delete avatar

        if (user instanceof Specialist) {
            specialistRepository.delete((Specialist) user); // Directly delete Specialist
        }
        userRepository.delete(user);
        logger.info("User (or Specialist) deleted with email: {}", email);
    }

    public void updatePassword(String email, UpdatePasswordRequest updatePasswordRequest, UserDetails userDetails) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        if (!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user); // Save password change
    }


    public void updateResetToken(User user) {
        userRepository.save(user);
    }

    //    ADMIN SERVICES
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO); // More concise
    }

    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDTO) // Use map for more concise code
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public UserDTO updateUserById(Long userId, UserUpdateDTO userUpdateDTO, String avatarUrl, String avatarPublicId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        updateUserFields(currentUser, userUpdateDTO); // Reuse helper method
        updateAddress(currentUser, userUpdateDTO);        // Reuse helper method

        if (avatarUrl != null && avatarPublicId != null) {
            currentUser.setAvatarUrl(avatarUrl);
            currentUser.setAvatarPublicId(avatarPublicId);
        }

        return userMapper.toDTO(currentUser); // Return DTO
    }

    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        deleteAvatar(user); // Reuse helper method

        if (user instanceof Specialist) {
            specialistRepository.delete((Specialist) user); // Directly delete Specialist
        }

        userRepository.delete(user);
        logger.info("User (or Specialist) deleted with ID: {}", userId);
    }

    public void updateUserRoles(Long userId, Set<Role.RoleName> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        roleAssignmentUtil.assignRoles(user, roleNames);
        userRepository.save(user);
        logger.info("Role names assigned for user with ID: {}", userId);
    }

    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchByKeyword(keyword, pageable).map(userMapper::toDTO); // More concise
    }


    // --- Helper Methods ---

    private void updateUserFields(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getFirstName() != null) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null) user.setLastName(userUpdateDTO.getLastName());
        if (userUpdateDTO.getEmail() != null) user.setEmail(userUpdateDTO.getEmail());
        if (userUpdateDTO.getMobileNumber() != null) user.setMobileNumber(userUpdateDTO.getMobileNumber());
    }

    private void updateAddress(User user, UserUpdateDTO userUpdateDTO) {
        Address address = user.getAddress() != null ? user.getAddress() : new Address();

        if (userUpdateDTO.getPincode() != null) address.setPincode(userUpdateDTO.getPincode());
        if (userUpdateDTO.getCity() != null) address.setCity(userUpdateDTO.getCity());
        if (userUpdateDTO.getState() != null) address.setState(userUpdateDTO.getState());
        if (userUpdateDTO.getCountry() != null) address.setCountry(userUpdateDTO.getCountry());
        if (userUpdateDTO.getLocality() != null) address.setLocality(userUpdateDTO.getLocality());

        if (userUpdateDTO.getPincode() != null || userUpdateDTO.getCity() != null || userUpdateDTO.getState() != null ||
                userUpdateDTO.getCountry() != null || userUpdateDTO.getLocality() != null) {
            user.setAddress(address);
        }
    }

    private void handleProfileImage(User user, MultipartFile profileImage) {
        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                logger.info("Uploading/Updating profile image for user: {}", user.getUserId());
                Map<String, Object> uploadResult;

                if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().isEmpty()) {
                    uploadResult = uploadService.updateImage(user.getAvatarPublicId(), profileImage, UploadService.ProfileType.USER);
                } else {
                    uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.USER);
                }

                user.setAvatarUrl((String) uploadResult.get("url"));
                user.setAvatarPublicId((String) uploadResult.get("public_id"));
            }
        } catch (IOException e) {
            logger.error("Error uploading/updating profile image: {}", e.getMessage(), e);
            throw new ImageUploadException("Error uploading profile image", e); // Re-throw custom exception
        }
    }

    private void deleteAvatar(User user) {
        if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().isEmpty()) {
            try {
                uploadService.deleteImage(user.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", user.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e); // Re-throw custom exception
            }
        }
    }
}
