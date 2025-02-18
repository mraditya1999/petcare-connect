package com.petconnect.backend.services;

import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.user.UserUpdateDTO;
import com.petconnect.backend.dto.user.UpdatePasswordRequestDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ImageDeletionException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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

    public UserDTO getUserProfile(String email) {
        logger.info("Fetching profile for user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.SPECIALIST)) {
                return specialistMapper.toSpecialistResponseDTO((Specialist) user);
            }

            return userMapper.toDTO(user);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while fetching profile for user with email: {}", email, e);
            throw new RuntimeException("An error occurred while fetching user profile", e);
        }
    }

    public UserDTO updateUserProfile(String username, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) throws IOException {
        logger.info("Updating profile for user with email: {}", username);

        try {
            User currentUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + username));

            updateUserFields(currentUser, userUpdateDTO);
            updateAddress(currentUser, userUpdateDTO);
            if (profileImage != null) {
                if (profileImage.isEmpty()) {
                    // Remove profile image
                    deleteAvatar(currentUser);
                    currentUser.setAvatarUrl(null);
                    currentUser.setAvatarPublicId(null);
                    logger.info("Profile image removed for user with email: {}", username);
                } else {
                    // Update profile image
                    handleProfileImageUpload(profileImage, currentUser);
                }
            } else {
                logger.info("No profile image provided for user with email: {}", username);
            }

            userRepository.save(currentUser);
            UserDTO updatedUserDTO = userMapper.toDTO(currentUser);
            logger.info("Profile updated and saved for user with email: {}", username);
            return updatedUserDTO;
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", username, e);
            throw e;
        } catch (IOException e) {
            logger.error("Error uploading profile image for user with email: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating profile for user with email: {}", username, e);
            throw new RuntimeException("An error occurred while updating user profile", e);
        }
    }

    public void deleteUserProfile(UserDetails userDetails) {
        String email = userDetails.getUsername();
        logger.info("Received request to delete profile for user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            deleteAvatar(user);

            if (user instanceof Specialist) {
                specialistRepository.delete((Specialist) user);
            } else {
                userRepository.delete(user);
            }
            logger.info("User profile deleted successfully for user with email: {}", email);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while deleting profile for user with email: {}", email, e);
            throw new RuntimeException("An error occurred while deleting user profile", e);
        }
    }

    public void updatePassword(String email, UpdatePasswordRequestDTO updatePasswordRequestDTO, UserDetails userDetails) {
        logger.info("Received request to update password for user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            if (!passwordEncoder.matches(updatePasswordRequestDTO.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }

            if (passwordEncoder.matches(updatePasswordRequestDTO.getNewPassword(), user.getPassword())) {
                throw new IllegalArgumentException("New password cannot be the same as the old password.");
            }

            user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.getNewPassword()));
            userRepository.save(user);
            logger.info("Password updated successfully for user with email: {}", email);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid password update request for user with email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating password for user with email: {}", email, e);
            throw new RuntimeException("An error occurred while updating password", e);
        }
    }

    public void updateResetToken(User user) {
        try {
            userRepository.save(user);
            logger.info("Reset token updated for user with ID: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("An error occurred while updating reset token for user with ID: {}", user.getUserId(), e);
            throw new RuntimeException("An error occurred while updating reset token", e);
        }
    }

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        try {
            return userRepository.findAll(pageable).map(userMapper::toDTO);
        } catch (Exception e) {
            logger.error("An error occurred while fetching all users", e);
            throw new RuntimeException("An error occurred while fetching all users", e);
        }
    }

    public UserDTO getUserById(Long userId) {
        try {
            return userRepository.findById(userId)
                    .map(userMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while fetching user by ID: {}", userId, e);
            throw new RuntimeException("An error occurred while fetching user by ID", e);
        }
    }

    public UserDTO updateUserById(Long id, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) throws IOException {
        logger.info("Updating profile for user with ID: {}", id);

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + id));

            updateUserFields(user, userUpdateDTO);
            updateAddress(user, userUpdateDTO);

            if (profileImage != null) {
                if (profileImage.isEmpty()) {
                    // Remove profile image
                    deleteAvatar(user);
                    user.setAvatarUrl(null);
                    user.setAvatarPublicId(null);
                    logger.info("Profile image removed for user with email: {}", user.getEmail());
                } else {
                    // Update profile image
                    handleProfileImageUpload(profileImage, user);
                }
            } else {
                logger.info("No profile image provided for user with email: {}", user.getEmail());
            }

            userRepository.save(user);
            UserDTO updatedUserDTO = userMapper.toDTO(user);
            logger.info("Profile updated and saved for user with ID: {}", id);
            return updatedUserDTO;
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating profile for user with ID: {}", id, e);
            throw new RuntimeException("An error occurred while updating user profile", e);
        }
    }

    public void deleteUserById(Long userId) {
        logger.info("Received request to delete user with ID: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            deleteAvatar(user);

            if (user instanceof Specialist) {
                specialistRepository.delete((Specialist) user);
            } else {
                userRepository.delete(user);
            }
            logger.info("User (or Specialist) deleted with ID: {}", userId);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while deleting user with ID: {}", userId, e);
            throw new RuntimeException("An error occurred while deleting user", e);
        }
    }

    public void updateUserRoles(Long userId, Set<Role.RoleName> roleNames) {
        logger.info("Updating roles for user with ID: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            roleAssignmentUtil.assignRoles(user, roleNames);
            userRepository.save(user);
            logger.info("Roles updated successfully for user with ID: {}", userId);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating roles for user with ID: {}", userId, e);
            throw new RuntimeException("An error occurred while updating user roles", e);
        }
    }

    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        logger.info("Searching users with keyword: {}", keyword);

        try {
            return userRepository.searchByKeyword(keyword, pageable).map(userMapper::toDTO);
        } catch (Exception e) {
            logger.error("An error occurred while searching users with keyword: {}", keyword, e);
            throw new RuntimeException("An error occurred while searching users", e);
        }
    }

    public Page<UserDTO> getAllUsersByRole(String roleName, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<User> usersPage = userRepository.findAllByRole(roleName, pageable);
        return usersPage.map(userMapper::toDTO); // Assuming you have a UserMapper to convert User to UserDTO
    }

    // --- Helper Methods ---

    private void updateUserFields(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getFirstName() != null) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null) user.setLastName(userUpdateDTO.getLastName());
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

    private void handleProfileImageUpload(MultipartFile profileImage, User user) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().isEmpty()) {
                // Delete the old profile image before uploading the new one
                uploadService.deleteImage(user.getAvatarPublicId());
            }
            Map<String, String> imageInfo = uploadProfileImage(profileImage);
            user.setAvatarUrl(imageInfo.get("avatarUrl"));
            user.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }
    }

    private Map<String, String> uploadProfileImage(MultipartFile profileImage) throws IOException {
        Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.USER);
        return Map.of(
                "avatarUrl", (String) uploadResult.get("url"),
                "avatarPublicId", (String) uploadResult.get("public_id")
        );
    }

    private void deleteAvatar(User user) {
        if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().isEmpty()) {
            try {
                uploadService.deleteImage(user.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", user.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e);
            }
        }
    }
}
