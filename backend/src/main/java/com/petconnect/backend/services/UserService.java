package com.petconnect.backend.services;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.AddressMapper;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UploadService uploadService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SpecialistMapper specialistMapper;
    private final SpecialistRepository specialistRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserMapper userMapper, UploadService uploadService, AddressRepository addressRepository, AddressMapper addressMapper, SpecialistMapper specialistMapper, SpecialistRepository specialistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
        this.specialistMapper = specialistMapper;
        this.specialistRepository = specialistRepository;
    }

    public Object getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        for (Role role : user.getRoles()) {
            if (role.getRoleName().equals(Role.RoleName.SPECIALIST)) {
                return specialistMapper.toSpecialistResponseDTO((Specialist) user);
            }
        }

        return userMapper.toDTO(user);
    }

    public UserDTO updateUserProfile(
            String username,
            String firstName,
            String lastName,
            String email,
            String mobileNumber,
            Long pincode,
            String city,
            String state,
            String country,
            String locality,
            MultipartFile profileImage) throws IOException {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + username));

        if (firstName != null) currentUser.setFirstName(firstName);
        if (lastName != null) currentUser.setLastName(lastName);
        if (email != null) currentUser.setEmail(email);
        if (mobileNumber != null) currentUser.setMobileNumber(mobileNumber);

        Address address = currentUser.getAddress() != null ? currentUser.getAddress() : new Address();

        if (pincode != null) address.setPincode(pincode);
        if (city != null) address.setCity(city);
        if (state != null) address.setState(state);
        if (country != null) address.setCountry(country);
        if (locality != null) address.setLocality(locality);

        if (pincode != null || city != null || state != null || country != null || locality != null) {
            currentUser.setAddress(address);
        }

        if (profileImage != null) {
            logger.info("Uploading new profile image for user: {}", currentUser.getUserId());
            Map<String, Object> uploadResult;
            if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
                uploadResult = uploadService.updateImage(currentUser.getAvatarPublicId(), profileImage, UploadService.ProfileType.USER);
            } else {
                uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.USER);
            }
            currentUser.setAvatarUrl((String) uploadResult.get("url"));
            currentUser.setAvatarPublicId((String) uploadResult.get("public_id"));
        }

        userRepository.save(currentUser);
        return userMapper.toDTO(currentUser);
    }
    @Transactional
    public void deleteUserProfile(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Check and delete the user's avatar if it exists
        if (user.getAvatarPublicId() != null) {
            try {
                uploadService.deleteImage(user.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", user.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e);
            }
        }

        if (user instanceof Specialist) {
            Specialist specialist = (Specialist) user;
            specialistRepository.delete(specialist);
        }

        userRepository.delete(user);
        logger.info("User (or Specialist) deleted with email: {}", email);

        ResponseEntity.ok(new ApiResponse<>("User profile deleted successfully."));
    }


    @Transactional
    public void updatePassword(String email, UpdatePasswordRequest updatePasswordRequest, UserDetails userDetails) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        if (!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public void updateResetToken(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
