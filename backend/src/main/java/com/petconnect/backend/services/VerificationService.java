package com.petconnect.backend.services;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TempUserStore tempUserStore;
    private final RoleAssignmentUtil roleAssignmentUtil;

    @Autowired
    public VerificationService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, EmailService emailService, TempUserStore tempUserStore, RoleAssignmentUtil roleAssignmentUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tempUserStore = tempUserStore;
        this.roleAssignmentUtil = roleAssignmentUtil;
    }

    /**
     * Verifies a user using the provided verification token.
     *
     * @param verificationToken the verification token
     * @return true if the user is successfully verified, false otherwise
     */
    @Transactional
    public boolean verifyUser(String verificationToken) {
        User tempUser = tempUserStore.getTemporaryUser(verificationToken);
        if (tempUser != null) {
            logger.info("Verifying user with token: {}", verificationToken);

            tempUser.setVerified(true);
            tempUser.setVerificationToken(null);

            assignRolesToUser(tempUser);

            userRepository.save(tempUser);
            logger.info("User verified and saved with token: {}", verificationToken);
            return true;
        } else {
            logger.warn("Verification token invalid or expired: {}", verificationToken);
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }
    }

    public void assignRolesToUser(User user) {
        boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles = new HashSet<>();
        roles.add(Role.RoleName.USER);
        if (isFirstVerifiedUser) {
            roles.add(Role.RoleName.ADMIN);
        }
        roleAssignmentUtil.assignRoles(user, roles);
    }

    /**
     * Resets the user's password using the provided reset token and new password.
     *
     * @param resetToken  the reset token
     * @param newPassword the new password
     * @return true if the password is successfully reset, false otherwise
     */
    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        logger.info("Reset token received: {}", resetToken);

        User user = userRepository.findByResetToken(resetToken).orElse(null);

        if (user == null) {
            logger.warn("Invalid reset token: {}", resetToken);
            throw new ResourceNotFoundException("Invalid reset token.");
        }

        logger.info("User found for reset token: {}", user.getEmail());

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("New password cannot be the same as the old password.");
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Nullify only after successful reset
        userRepository.save(user);

        logger.info("Password reset successful for user: {}", user.getEmail());
        return true;
    }

    /**
     * Sends a verification email to the user.
     *
     * @param user the user to send the email to
     */
    public void sendVerificationEmail(User user) {
        user.setVerificationToken(UUID.randomUUID().toString());
        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
        emailService.sendVerificationEmail(user);
    }
}
