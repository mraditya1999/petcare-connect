package com.petconnect.backend.services;

import com.petconnect.backend.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Value("${frontend-url}")
    private String frontendUrl;

    @Value("${backend-url}")
    private String backendUrl;

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(User user) {
        String verificationLink = frontendUrl + "/user/verify-email?token=" + user.getVerificationToken() + "&email=" + user.getEmail();
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
            helper.setFrom("ay5480620@gmail.com");
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error sending verification email", e);
        }
    }

    public void sendResetEmail(User user) {
        String resetLink = frontendUrl + "/user/reset-password?token=" + user.getResetToken();
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
            helper.setFrom("ay5480620@gmail.com");
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error sending reset email", e);
        }
    }
}
