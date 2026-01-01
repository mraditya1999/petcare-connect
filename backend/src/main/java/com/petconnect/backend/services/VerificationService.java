package com.petconnect.backend.services;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.jpa.UserRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import com.petconnect.backend.exceptions.IllegalArgumentException;

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
     *
     * @param verificationToken the verification token (must not be null or blank)
     * @throws IllegalArgumentException  if verificationToken is null or blank
     * @throws ResourceNotFoundException if token is invalid/expired or user is not found
     */
    @Transactional
    public void verifyUser(String verificationToken) {
        if (verificationToken == null || verificationToken.isBlank()) {
            throw new IllegalArgumentException("Verification token cannot be null or blank");
        }
        
        try {
            String email = redisStorageService.getVerificationEmail(verificationToken);
            if (email == null) {
                logger.warn("Invalid or expired verification token");
                throw new ResourceNotFoundException("Invalid or expired verification token");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found for email: {}", email);
                        return new ResourceNotFoundException("User not found");
                    });

            if (user.isVerified()) {
                logger.info("User already verified: {}", user.getEmail());
                redisStorageService.deleteVerificationToken(verificationToken);
                return;
            }

            user.setVerified(true);
            boolean isFirstVerifiedUser = userRepository.countByVerified(true) == 0;
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
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error verifying user with token", e);
            throw new RuntimeException("Failed to verify user", e);
        }
    }

    /**
     * Resets the user's password using the provided reset token and new password.
     *
     * @param resetToken the reset token (must not be null or blank)
     * @param newPassword the new password (must not be null or blank)
     * @return true if password reset is successful
     * @throws IllegalArgumentException if resetToken or newPassword is null/blank, or new password is invalid
     * @throws ResourceNotFoundException if token is invalid/expired or user is not found
     */
    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException("Reset token cannot be null or blank");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or blank");
        }
        
        try {
            String email = redisStorageService.getResetEmail(resetToken);
            if (email == null) {
                logger.warn("Reset token invalid or expired");
                throw new ResourceNotFoundException("Invalid or expired reset token");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found for email: {}", email);
                        return new ResourceNotFoundException("User not found");
                    });

            // Validate password strength if needed
            if (user.getPassword() != null && passwordEncoder.matches(newPassword, user.getPassword())) {
                logger.warn("New password cannot be the same as the old password for user {}", user.getEmail());
                throw new IllegalArgumentException("New password cannot be the same as the old password");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redisStorageService.deleteResetToken(resetToken); // single-use
            logger.info("Password reset successful for user: {}", user.getEmail());
            return true;
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error resetting password", e);
            throw new RuntimeException("Failed to reset password", e);
        }
    }

    /**
     * Sends a verification email to the user.
     *
     * @param user the user to send verification email to (must not be null)
     * @throws IllegalArgumentException if user is null or user email/token is invalid
     */
    public void sendVerificationEmail(User user) {
        try {
            emailService.sendVerificationEmail(user);
            logger.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error sending verification email to user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

}
