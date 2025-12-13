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
     */
    public void sendVerificationEmail(User user) {
        String[] urls = frontendUrls.split(",");
        String token = user.getVerificationToken();

        String verificationLink = chooseURL(urls) + "/user/verify-email?token=" + token;

        sendEmail(user,
                "Email Verification",
                "Thank you for registering. Please verify your email address by clicking below:",
                verificationLink,
                "Verify Email");
    }

    /**
     * Sends a password reset email to the user.
     */
    public void sendResetEmail(User user) {
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
    }

    /**
     * Generic reusable email sender using unified Thymeleaf template.
     */
    private void sendEmail(User user, String subject, String message, String actionLink, String buttonText) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("title", subject);
        context.setVariable("message", message);
        context.setVariable("actionLink", actionLink);
        context.setVariable("buttonText", buttonText);

        String htmlContent = templateEngine.process("email-template", context);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromAddress); // externalized sender
            mailSender.send(mimeMessage);
            logger.info("{} email sent to: {}", subject, user.getEmail());
        } catch (MessagingException e) {
            logger.error("Error sending {} email to: {}", subject, user.getEmail(), e);
            throw new EmailSendException("Failed to send " + subject + " email", e);
        }
    }

    /**
     * Chooses a URL from the provided array of URLs.
     */
    private String chooseURL(String[] urls) {
        if (urls == null || urls.length == 0) {
            throw new IllegalArgumentException("No frontend URLs configured.");
        }
        String chosenURL = urls[0].trim();
        if (!isValidURL(chosenURL)) {
            throw new IllegalArgumentException("Invalid URL: " + chosenURL);
        }
        return chosenURL;
    }

    /**
     * Validates if the provided URL is valid.
     */
    private boolean isValidURL(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            uri.toURL();
            return true;
        } catch (java.net.MalformedURLException | java.net.URISyntaxException e) {
            logger.error("Invalid URL format: {}", url, e);
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
