package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.EmailType;
import com.spring.petcareConnect.exceptions.EmailSendException;
import com.spring.petcareConnect.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.from}")
    private String fromAddress;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(User user, EmailType type) {
        logger.info("Preparing to send {} email to {}", type, user.getEmail());

        try {
            String subject;
            String message;
            String actionLink;
            String buttonText;
            String template = "email-template";

            switch (type) {
                case VERIFICATION:
                    subject = "Email Verification";
                    message = "Thank you for registering. Please verify your email address by clicking below:";
                    actionLink = frontendUrl + "/user/verify-email?token=" + user.getVerificationToken();
                    buttonText = "Verify Email";
                    logger.debug("Verification email prepared for user {}", user.getUserId());
                    break;

                case RESET:
                    subject = "Password Reset";
                    message = "You requested a password reset. Click below to set a new password:";
                    actionLink = frontendUrl + "/user/reset-password?token=" + user.getResetToken();
                    buttonText = "Reset Password";
                    logger.debug("Reset email prepared for user {}", user.getUserId());
                    break;

                default:
                    logger.error("Unsupported email type {} for user {}", type, user.getUserId());
                    throw new IllegalArgumentException("Unsupported email type: " + type);
            }

            sendEmailInternal(user, subject, message, actionLink, buttonText, template);

            logger.info("{} email successfully sent to {}", type, user.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send {} email to {}", type, user.getEmail(), e);
            throw new EmailSendException("Failed to send " + type + " email", e);
        }
    }

    private void sendEmailInternal(User user, String subject, String message, String actionLink, String buttonText, String template) {
        logger.debug("Building email content for subject={} user={}", subject, user.getUserId());

        try {
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("title", subject);
            context.setVariable("message", message);
            context.setVariable("actionLink", actionLink);
            context.setVariable("buttonText", buttonText);

            if (template.equals("reset-email") && user.getResetTokenExpiry() != null) {
                context.setVariable("expiry", user.getResetTokenExpiry());
                logger.debug("Added reset token expiry for user {}", user.getUserId());
            }

            String htmlContent = templateEngine.process(template, context);
            logger.trace("Generated HTML content for email: {}", htmlContent);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromAddress);

            mailSender.send(mimeMessage);
            logger.info("Email with subject '{}' sent to {}", subject, user.getEmail());

        } catch (Exception e) {
            logger.error("Error while sending {} email to {}", subject, user.getEmail(), e);
            throw new EmailSendException("Failed to send " + subject + " email", e);
        }
    }
}
