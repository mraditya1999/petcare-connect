package com.petconnect.backend.services;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UploadService uploadService;
    private final AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserMapper userMapper, UploadService uploadService, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
        this.addressRepository = addressRepository;
    }

    public UserDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserDTO updateUserProfile(User currentUser, UserDTO userDTO) {
        logger.info("Updating user profile for userId: {}", currentUser.getUserId());

        try {
            if (userDTO.getFirstName() != null) {
                logger.debug("Updating firstName: {}", userDTO.getFirstName());
                currentUser.setFirstName(userDTO.getFirstName());
            }

            if (userDTO.getLastName() != null) {
                logger.debug("Updating lastName: {}", userDTO.getLastName());
                currentUser.setLastName(userDTO.getLastName());
            }

            if (userDTO.getEmail() != null) {
                logger.debug("Updating email: {}", userDTO.getEmail());
                currentUser.setEmail(userDTO.getEmail());
            }

            if (userDTO.getMobileNumber() != null) {
                logger.debug("Updating mobileNumber: {}", userDTO.getMobileNumber());
                currentUser.setMobileNumber(userDTO.getMobileNumber());
            }

            if (userDTO.getAvatarUrl() != null) {
                logger.debug("Updating avatarUrl: {}", userDTO.getAvatarUrl());
                currentUser.setAvatarUrl(userDTO.getAvatarUrl());
            }
            if (userDTO.getAvatarPublicId() != null) {
                logger.debug("Updating avatarPublicId: {}", userDTO.getAvatarPublicId());
                currentUser.setAvatarPublicId(userDTO.getAvatarPublicId());
            }

                AddressDTO addressDTO = userDTO.getAddress();
            if (addressDTO != null) {
                logger.info("Updating address...");
                Address address = currentUser.getAddress();

                if (address == null) {
                    logger.debug("Creating new address object");
                    address = new Address();
                    currentUser.setAddress(address);
                }

                if (addressDTO.getPincode() != null) {
                    logger.debug("Updating pincode: {}", addressDTO.getPincode());
                    address.setPincode(addressDTO.getPincode());
                }
                if (addressDTO.getCity() != null) {
                    logger.debug("Updating city: {}", addressDTO.getCity());
                    address.setCity(addressDTO.getCity());
                }
                if (addressDTO.getState() != null) {
                    logger.debug("Updating state: {}", addressDTO.getState());
                    address.setState(addressDTO.getState());
                }
                if (addressDTO.getCountry() != null) {
                    logger.debug("Updating country: {}", addressDTO.getCountry());
                    address.setCountry(addressDTO.getCountry());
                }
                if (addressDTO.getLocality() != null) {
                    logger.debug("Updating locality: {}", addressDTO.getLocality());
                    address.setLocality(addressDTO.getLocality());
                }

                logger.info("Saving address...");
//                addressRepository.save(address);
//                currentUser.setAddress(address);
            }

            logger.info("Saving user...");
            userRepository.save(currentUser);

            logger.info("User profile updated successfully for userId: {}", currentUser.getUserId());
            return userMapper.toDTO(currentUser);
        } catch (Exception e) {
            logger.error("Error updating user profile: ", e);
            throw e; // Rethrow the exception to check the stack trace
        }
    }

    public void deleteUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        // Delete the profile image from Cloudinary
        if (user.getAvatarPublicId() != null) {
            try {
                uploadService.deleteImage(user.getAvatarPublicId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userRepository.delete(user);
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

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

//    public Optional<User> findById(Long userId) {
//        return userRepository.findById(userId);
//    }

//    public User addRoleToUser(Long userId, Role.RoleName roleName) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            Role role = new Role();
//            role.setRoleName(roleName);
//            user.getRoles().add(role);
//            return userRepository.save(user);
//        }
//        return null;
//    }
//
//    public User removeRoleFromUser(Long userId, Role.RoleName roleName) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            user.getRoles().removeIf(role -> role.getRoleName().equals(roleName));
//            return userRepository.save(user);
//        }
//        return null;
//    }

//    public UserDTO getUserDTO(String email) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isEmpty()) {
//            throw new ResourceNotFoundException("User not found with email: " + email);
//        }
//        return userMapper.toDTO(userOptional.get());
//    }
}
