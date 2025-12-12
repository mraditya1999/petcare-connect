package com.petconnect.backend.services;

import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.user.UserUpdateDTO;
import com.petconnect.backend.dto.user.UpdatePasswordRequestDTO;
import com.petconnect.backend.entity.*;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.AddressMapper;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import com.petconnect.backend.utils.PhoneUtils;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.validators.PasswordValidator;
import jakarta.transaction.Transactional;
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
    private final PetRepository petRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleAssignmentUtil roleAssignmentUtil, UserMapper userMapper, UploadService uploadService, SpecialistMapper specialistMapper, SpecialistRepository specialistRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
        this.specialistMapper = specialistMapper;
        this.specialistRepository = specialistRepository;
        this.petRepository = petRepository;
    }

    /**
     * Fetches the profile for a user based on their email address.
     *
     * @param email the email address of the user
     * @return UserDTO containing the user profile information
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO getUserProfile(String email) {
        logger.info("Fetching profile for user with email: {}", email);

        try {
            User user = userRepository.findDetailedByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.SPECIALIST)) {
                return specialistMapper.toSpecialistResponseDTO((Specialist) user);
            }

            return userMapper.toDTO(user);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            throw e;
        }
    }

    /**
     * Updates the profile for a user based on their username.
     *
     * @param username the username (email) of the user
     * @param userUpdateDTO the data transfer object containing updated user information
     * @param profileImage the profile image to be updated (optional)
     * @return UserDTO containing the updated user profile information
     * @throws IOException if an error occurs while uploading the profile image
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO updateUserProfile(String username, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) throws IOException {
        logger.info("Updating profile for user with email: {}", username);

        try {
            User currentUser = userRepository.findDetailedByEmail(username)
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
        }
    }

    /**
     * Deletes the profile for a user based on their email address.
     *
     * @param userDetails the user details of the authenticated user
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException if an error occurs while deleting the user profile
     */
    @Transactional
    public void deleteUserProfile(UserDetails userDetails) {
        String email = userDetails.getUsername();
        logger.info("Received request to delete profile for user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            deleteAvatar(user);
            deletePetAvatars(user);

            userRepository.delete(user);
            logger.info("User profile deleted successfully for user with email: {}", email);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            throw e;
        }
    }


    /**
     * Deletes pet profiles associated with the user.
     *
     * @param user the user whose pet profiles are to be deleted
     */
    @Transactional
    private void deletePetAvatars(User user) {
        List<Pet> pets = petRepository.findAllByPetOwner(user);
        for (Pet pet : pets) {
            if (pet.getAvatarPublicId() != null && !pet.getAvatarPublicId().isEmpty()) {
                try {
                    uploadService.deleteImage(pet.getAvatarPublicId());
                } catch (IOException e) {
                    logger.error("Error deleting avatar for pet with ID: {}", pet.getPetId(), e);
                    throw new ImageDeletionException("Error deleting avatar image", e);
                }
            }
        }
    }

    /**
     * Updates the password for a user based on their email address.
     *
     * @param email the email address of the user
     * @param updatePasswordRequestDTO the data transfer object containing the current and new password
     * @param userDetails the user details of the authenticated user
     * @throws ResourceNotFoundException if the user is not found
     * @throws IllegalArgumentException if the current password is incorrect or the new password is the same as the old password
     * @throws RuntimeException if an error occurs while updating the password
     */
    @Transactional
    public void updatePassword(String email, UpdatePasswordRequestDTO updatePasswordRequestDTO, UserDetails userDetails) {
        logger.info("Received request to update password for user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

            boolean isLocalUser = user.getOauthAccounts()
                    .stream()
                    .anyMatch(acc -> acc.getProvider() == OAuthAccount.AuthProvider.LOCAL);

            if (!PasswordValidator.isValid(updatePasswordRequestDTO.getNewPassword())) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
            }


            if (isLocalUser) {
                if (!passwordEncoder.matches(updatePasswordRequestDTO.getCurrentPassword(), user.getPassword())) {
                    throw new IllegalArgumentException("Current password is incorrect.");
                }
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
        }
    }

    /**
     * Updates the reset token for a user.
     *
     * @param user the user whose reset token is to be updated
     * @throws RuntimeException if an error occurs while updating the reset token
     */
    @Transactional
    public void updateResetToken(User user) {
        userRepository.save(user);
        logger.info("Reset token updated for user with ID: {}", user.getUserId());
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable the pagination information
     * @return a page of UserDTOs containing user information
     * @throws RuntimeException if an error occurs while fetching all users
     */
    @Transactional
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return UserDTO containing the user information
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException if an error occurs while fetching the user by ID
     */
    @Transactional
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Updates the profile for a user based on their ID.
     *
     * @param id the ID of the user
     * @param userUpdateDTO the data transfer object containing updated user information
     * @param profileImage the profile image to be updated (optional)
     * @return UserDTO containing the updated user profile information
     * @throws IOException if an error occurs while uploading the profile image
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException if an error occurs while updating the user profile
     */
    @Transactional
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
        }
    }

    /**
     * Deletes a user based on their ID.
     *
     * @param userId the ID of the user to be deleted
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException if an error occurs while deleting the user
     */
    @Transactional
    public void deleteUserById(Long userId) {
        logger.info("Received request to delete user with ID: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            deleteAvatar(user);
            deletePetAvatars(user);

            userRepository.delete(user);
            logger.info("User (or Specialist) deleted with ID: {}", userId);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Updates the roles for a user based on their ID.
     *
     * @param userId the ID of the user
     * @param roleNames the set of role names to be assigned to the user
     * @throws ResourceNotFoundException if the user is not found
     * @throws RuntimeException if an error occurs while updating the user roles
     */
    @Transactional
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
        }
    }

    /**
     * Searches for users based on a keyword.
     *
     * @param keyword the search keyword
     * @param pageable the pagination information
     * @return a page of UserDTOs containing the search results
     * @throws RuntimeException if an error occurs while searching users
     */
    @Transactional
    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        logger.info("Searching users with keyword: {}", keyword);
        return userRepository.searchByKeyword(keyword, pageable).map(userMapper::toDTO);
    }

    /**
     * Retrieves all users with a specific role and pagination.
     *
     * @param roleName the role name to filter users
     * @param page the page number to retrieve
     * @param size the number of items per page
     * @param sortBy the field to sort by
     * @param sortDir the sort direction (ascending or descending)
     * @return a page of UserDTOs containing the filtered users
     */
    public Page<UserDTO> getAllUsersByRole(String roleName, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<User> usersPage = userRepository.findAllByRole(roleName, pageable);
        return usersPage.map(userMapper::toDTO);
    }


    // --- Helper Methods ---

    /**
     * Updates the fields of a user based on the provided UserUpdateDTO.
     *
     * @param user the user whose fields are to be updated
     * @param userUpdateDTO the data transfer object containing updated user information
     */
    @Transactional
    private void updateUserFields(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getFirstName() != null) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null) user.setLastName(userUpdateDTO.getLastName());

        if (userUpdateDTO.getMobileNumber() != null) {
            String normalizedPhone = PhoneUtils.normalizeToE164(userUpdateDTO.getMobileNumber());

            if (normalizedPhone == null) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            if (!PhoneUtils.isValidE164(normalizedPhone)) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            user.setMobileNumber(normalizedPhone);
        }
    }

    /**
     * Updates the address of a user based on the provided UserUpdateDTO.
     *
     * @param user the user whose address is to be updated
     * @param userUpdateDTO the data transfer object containing updated address information
     */
    @Transactional
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

    /**
     * Handles the upload of a profile image for a user.
     *
     * @param profileImage the profile image to be uploaded
     * @param user the user for whom the profile image is to be uploaded
     * @throws IOException if an error occurs while uploading the profile image
     */
    @Transactional
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

    /**
     * Uploads a profile image and returns a map containing the avatar URL and public ID.
     *
     * @param profileImage the profile image to be uploaded
     * @return a map containing the avatar URL and public ID
     * @throws IOException if an error occurs while uploading the profile image
     */
    @Transactional
    private Map<String, String> uploadProfileImage(MultipartFile profileImage) throws IOException {
        Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.USER);
        return Map.of(
                "avatarUrl", (String) uploadResult.get("url"),
                "avatarPublicId", (String) uploadResult.get("public_id")
        );
    }

    /**
     * Deletes the avatar image of a user.
     *
     * @param user the user whose avatar image is to be deleted
     * @throws ImageDeletionException if an error occurs while deleting the avatar image
     */
    @Transactional
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
