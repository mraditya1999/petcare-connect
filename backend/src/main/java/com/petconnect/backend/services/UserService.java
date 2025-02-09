package com.petconnect.backend.services;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.UpdatePasswordRequest;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import jakarta.transaction.Transactional;
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


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserMapper userMapper, UploadService uploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.uploadService = uploadService;
    }

    public UserDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserDTO updateUserProfile(User user, UserDTO userDTO) {

        // Update user fields if present
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        if (userDTO.getAvatarPublicId() != null) {
            user.setAvatarPublicId(userDTO.getAvatarPublicId());
        }
        if (userDTO.getMobileNumber() != null) {
            user.setMobileNumber(userDTO.getMobileNumber());
        }

        // Update address fields if present
        AddressDTO addressDTO = userDTO.getAddress();
        if (addressDTO != null) {
            Address address = user.getAddress();
            if (address == null) {
                // Create new address if it doesn't exist
                address = new Address();
                user.setAddress(address);
            }
            if (addressDTO.getPincode() != null) {
                address.setPincode(addressDTO.getPincode());
            }
            if (addressDTO.getCity() != null) {
                address.setCity(addressDTO.getCity());
            }
            if (addressDTO.getState() != null) {
                address.setState(addressDTO.getState());
            }
            if (addressDTO.getCountry() != null) {
                address.setCountry(addressDTO.getCountry());
            }
            if (addressDTO.getLocality() != null) {
                address.setLocality(addressDTO.getLocality());
            }
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
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

    public Optional<User> getUserById(Long userId) {

        return Optional.empty();
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
