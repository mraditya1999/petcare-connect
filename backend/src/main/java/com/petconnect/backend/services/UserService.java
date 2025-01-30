package com.petconnect.backend.services;

import com.petconnect.backend.config.TempUserStore;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.security.UserPrincipal;
import com.petconnect.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempUserStore tempUserStore;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, TempUserStore tempUserStore, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tempUserStore = tempUserStore;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        // Check if the user is the first user
        boolean isFirstUser = userRepository.count() == 0;

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (isFirstUser) {
            Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role userRole = roleRepository.findByRoleName(Role.RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));
            roles.add(adminRole);
            roles.add(userRole);
        } else {
            Role defaultRole = roleRepository.findByRoleName(Role.RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        // Save the user temporarily
        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
        emailService.sendVerificationEmail(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public boolean verifyUser(String verificationToken) {
        System.out.println("verificationToken: " + verificationToken);
        User tempUser = tempUserStore.getTemporaryUser(verificationToken);
        if (tempUser != null) {
            tempUser.setVerified(true);
            tempUser.setVerificationToken(null); // Clear the token
            userRepository.save(tempUser);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String resetToken, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(resetToken);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
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

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }
        return Optional.empty();
    }

    public void sendVerificationEmail(User user) {
        emailService.sendVerificationEmail(user);
    }

    public void sendResetEmail(User user) {
        emailService.sendResetEmail(user);
    }

    public boolean existsByEmail(String email) {
       return userRepository.existsByEmail(email);
    }

    public String generateJwtToken(User user) {
        UserDetails userDetails = new UserPrincipal(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::getRoleName).toArray());

        return jwtUtil.generateToken(claims, userDetails.getUsername());
    }
}
