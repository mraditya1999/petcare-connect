package com.petconnect.backend.services;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserDTO updateUserProfile(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

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

    public void updateResetToken(User user) {
        userRepository.save(user);
    }

    public void deleteUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

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
