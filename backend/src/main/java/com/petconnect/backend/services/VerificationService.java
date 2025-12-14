package com.petconnect.backend.services;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final RedisStorageService redisStorageService;

    @Autowired
    public VerificationService(UserRepository userRepository,
                               @Lazy PasswordEncoder passwordEncoder,
                               EmailService emailService,
                               RoleAssignmentUtil roleAssignmentUtil,
                               RedisStorageService redisStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.redisStorageService = redisStorageService;
    }

    /**
     * Verifies a user using the provided verification token.
     */
    @Transactional
    public boolean verifyUser(String verificationToken) {
        String email = redisStorageService.getVerificationEmail(verificationToken);
        if (email == null) {
            logger.warn("Invalid or expired verification token.");
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setVerified(true);

        boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles;
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.SPECIALIST)) {
            roles = Set.of(Role.RoleName.USER, Role.RoleName.SPECIALIST);
        } else {
            roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
        }
        roleAssignmentUtil.assignRoles(user, roles);

        userRepository.save(user);
        redisStorageService.deleteVerificationToken(verificationToken);
        logger.info("User verified successfully: {}", user.getEmail());
        return true;
    }

    /**
     * Resets the user's password using the provided reset token and new password.
     */
    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        String email = redisStorageService.getResetEmail(resetToken);
        if (email == null) {
            logger.warn("Reset token invalid or expired");
            throw new ResourceNotFoundException("Invalid or expired reset token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("New password cannot be the same as the old password for user {}", user.getEmail());
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisStorageService.deleteResetToken(resetToken); // single-use
        logger.info("Password reset successful for user: {}", user.getEmail());
        return true;
    }

    /**
     * Sends a verification email to the user.
     */
    public void sendVerificationEmail(User user) {
        String token = user.getVerificationToken();
        redisStorageService.saveVerificationToken(token, user.getEmail(), java.time.Duration.ofHours(24));
        emailService.sendVerificationEmail(user);
    }

}
