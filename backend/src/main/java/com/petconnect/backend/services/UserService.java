package com.petconnect.backend.services;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User addRoleToUser(Long userId, Role.RoleName roleName) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Role role = new Role();
            role.setRoleName(roleName);
            user.getRoles().add(role);
            return userRepository.save(user);
        }
        return null;
    }

    public User removeRoleFromUser(Long userId, Role.RoleName roleName) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getRoles().removeIf(role -> role.getRoleName().equals(roleName));
            return userRepository.save(user);
        }
        return null;
    }

    public UserDTO getUserDTO(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return userMapper.toDTO(userOptional.get());
    }


}
