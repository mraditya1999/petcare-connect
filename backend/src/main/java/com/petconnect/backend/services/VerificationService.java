package com.petconnect.backend.services;

import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TempUserStore tempUserStore;

    @Autowired
    public VerificationService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, EmailService emailService, TempUserStore tempUserStore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tempUserStore = tempUserStore;
    }

    @Transactional
    public boolean verifyUser(String verificationToken) {
        User tempUser = tempUserStore.getTemporaryUser(verificationToken);
        if (tempUser != null) {
            logger.info("Verifying user with token: {}", verificationToken);

            tempUser.setVerified(true);
            tempUser.setVerificationToken(null);

            userRepository.save(tempUser);
            logger.info("User verified and saved with token: {}", verificationToken);
            return true;
        } else {
            logger.warn("Verification token invalid or expired: {}", verificationToken);
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }
    }

//    @Transactional
//    public boolean resetPassword(String resetToken, String newPassword) {
//        User user = userRepository.findByResetToken(resetToken)
//                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token."));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setResetToken(null);
//        userRepository.save(user);
//        logger.info("Password reset for user with token: {}", resetToken);
//        return true;
//    }

    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token."));

        // Assuming the old password is stored as a hash, compare the hashes
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("New password cannot be the same as the old password: {}", resetToken);
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
        logger.info("Password reset for user with token: {}", resetToken);
        return true;
    }


    public void sendVerificationEmail(User user) {
        user.setVerificationToken(UUID.randomUUID().toString());
        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
        emailService.sendVerificationEmail(user);
    }
}
