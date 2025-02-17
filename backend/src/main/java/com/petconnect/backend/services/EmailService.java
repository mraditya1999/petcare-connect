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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {

    @Value("${frontend.urls}")
    private String frontendUrls;

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(User user) {
        String[] urls = frontendUrls.split(",");
        String verificationLink = chooseURL(urls) + "/user/verify-email?token=" + user.getVerificationToken() + "&email=" + user.getEmail();
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("verificationLink", verificationLink);
        String htmlContent = templateEngine.process("verification-email", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Email Verification");
            helper.setText(htmlContent, true);
            helper.setFrom("ay5480620@gmail.com"); // Or configure in MailConfig
            mailSender.send(message);
            logger.info("Verification email sent to: {}", user.getEmail());

        } catch (MessagingException e) {
            logger.error("Error sending verification email to: {}", user.getEmail(), e);
        }
    }

    public void sendResetEmail(User user) {
        String[] urls = frontendUrls.split(",");
        String resetLink = chooseURL(urls) + "/user/reset-password?token=" + user.getResetToken() + "&email=" + user.getEmail();
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("reset-password", context);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Request");
            helper.setText(htmlContent, true);
            helper.setFrom("ay5480620@gmail.com"); // Or configure in MailConfig
            mailSender.send(message);
            logger.info("Password reset email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            logger.error("Error sending reset email to: {}", user.getEmail(), e);
        }
    }


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

    private boolean isValidURL(String url) {
        try {
            new java.net.URI(url).toURL();  // Use URI and toURL()
            return true;
        } catch (java.net.MalformedURLException | java.net.URISyntaxException e) {
            logger.error("Invalid URL format: {}", url, e);
            return false;
        }
    }
}