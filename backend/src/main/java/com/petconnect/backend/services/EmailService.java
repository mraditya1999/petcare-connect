package com.petconnect.backend.services;

import com.petconnect.backend.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${frontend.urls}")
    private String frontendUrls;

    @Value("${spring.mail.from}")
    private String fromAddress;

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final TemplateEngine templateEngine;
    private final RedisStorageService redisStorageService;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        RedisStorageService redisStorageService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.redisStorageService = redisStorageService;
    }

    /**
     * Sends a verification email to the user.
     *
     * @param user the user to send the verification email to (must not be null)
     * @throws IllegalArgumentException if user is null or user email/token is invalid
     * @throws EmailSendException if email sending fails
     */
    public void sendVerificationEmail(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or blank");
        }
        if (user.getVerificationToken() == null || user.getVerificationToken().isBlank()) {
            throw new IllegalArgumentException("Verification token cannot be null or blank");
        }
        
        try {
            String[] urls = frontendUrls.split(",");
            String token = user.getVerificationToken();
            String verificationLink = chooseURL(urls) + "/user/verify-email?token=" + token;

            sendEmail(user,
                    "Email Verification",
                    "Thank you for registering. Please verify your email address by clicking below:",
                    verificationLink,
                    "Verify Email");
        } catch (Exception e) {
            logger.error("Error sending verification email to user: {}", user.getEmail(), e);
            throw new EmailSendException("Failed to send verification email", e);
        }
    }

    /**
     * Sends a password reset email to the user.
     *
     * @param user the user to send the reset email to (must not be null)
     * @throws IllegalArgumentException if user is null or user email is invalid
     * @throws EmailSendException if email sending fails
     */
    public void sendResetEmail(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or blank");
        }
        
        try {
            String[] urls = frontendUrls.split(",");

            // Generate a cryptographically strong token
            String token = generateSecureToken();

            // Save token in Redis with TTL (15 minutes)
            redisStorageService.saveResetToken(token, user.getEmail(), Duration.ofMinutes(15));

            // Secure link: only token, no email
            String resetLink = chooseURL(urls) + "/user/reset-password?token=" + token;

            sendEmail(user,
                    "Password Reset Request",
                    "You requested a password reset. Please click the button below:",
                    resetLink,
                    "Reset Password");
        } catch (Exception e) {
            logger.error("Error sending reset email to user: {}", user.getEmail(), e);
            throw new EmailSendException("Failed to send reset email", e);
        }
    }

    /**
     * Generic reusable email sender using unified Thymeleaf template.
     *
     * @param user the user to send email to (must not be null)
     * @param subject the email subject (must not be null or blank)
     * @param message the email message (must not be null)
     * @param actionLink the action link URL (must not be null or blank)
     * @param buttonText the button text (must not be null or blank)
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws EmailSendException if email sending fails
     */
    private void sendEmail(User user, String subject, String message, String actionLink, String buttonText) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or blank");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (actionLink == null || actionLink.isBlank()) {
            throw new IllegalArgumentException("Action link cannot be null or blank");
        }
        if (buttonText == null || buttonText.isBlank()) {
            throw new IllegalArgumentException("Button text cannot be null or blank");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("From address is not configured");
        }
        
        try {
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("title", subject);
            context.setVariable("message", message);
            context.setVariable("actionLink", actionLink);
            context.setVariable("buttonText", buttonText);

            String htmlContent = templateEngine.process("email-template", context);
            if (htmlContent == null || htmlContent.isBlank()) {
                throw new IllegalStateException("Failed to process email template");
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromAddress);
            mailSender.send(mimeMessage);
            logger.info("{} email sent successfully to: {}", subject, user.getEmail());
        } catch (MessagingException e) {
            logger.error("Error sending {} email to: {}", subject, user.getEmail(), e);
            throw new EmailSendException("Failed to send " + subject + " email", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending {} email to: {}", subject, user.getEmail(), e);
            throw new EmailSendException("Failed to send " + subject + " email", e);
        }
    }

    /**
     * Chooses a URL from the provided array of URLs.
     *
     * @param urls the array of URLs (must not be null or empty)
     * @return the first valid URL from the array
     * @throws IllegalArgumentException if urls is null, empty, or contains no valid URLs
     */
    private String chooseURL(String[] urls) {
        if (urls == null || urls.length == 0) {
            throw new IllegalArgumentException("No frontend URLs configured");
        }
        String chosenURL = urls[0].trim();
        if (chosenURL.isBlank()) {
            throw new IllegalArgumentException("First frontend URL is blank");
        }
        if (!isValidURL(chosenURL)) {
            throw new IllegalArgumentException("Invalid URL: " + chosenURL);
        }
        return chosenURL;
    }

    /**
     * Validates if the provided URL is valid.
     *
     * @param url the URL to validate (must not be null)
     * @return true if the URL is valid, false otherwise
     */
    private boolean isValidURL(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            logger.debug("Invalid URL format: {}", url);
            return false;
        }
    }

    /**
     * Generate a cryptographically strong random token.
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Custom exception for email sending failures.
     */
    public static class EmailSendException extends RuntimeException {
        public EmailSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
