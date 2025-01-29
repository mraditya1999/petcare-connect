package com.petconnect.backend.controllers;

import com.petconnect.backend.entity.User;
import com.petconnect.backend.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JavaMailSender mailSender;

    @Autowired
    public AuthController(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    // Inside your AuthController
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/contact")
    public String getContact() {
        return "contact";
    }

@PostMapping("/register")
public ResponseEntity<String> registerUser(@RequestBody Map<String, String> userRequest) {
    // Create a new User instance
    User user = new User();

    // Map the 'name' attribute to 'firstName'
    user.setFirstName(userRequest.get("name"));
    user.setEmail(userRequest.get("email"));
    user.setPassword(userRequest.get("password"));

    // Log registration attempt
    logger.info("Registering user: {}", user);

    // Register the user
    userService.registerUser(user);

    // Return successful response
    return ResponseEntity.ok("User registered successfully. Please check your email for the verification link.");
}

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<User> authenticatedUser = userService.authenticateUser(email, password);

        return authenticatedUser.map(user -> ResponseEntity.ok("User logged in successfully: " + user.getUsername()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set expiry to immediate
        response.addCookie(cookie);

        return ResponseEntity.ok("User logged out successfully");
    }


    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyUser(@RequestBody Map<String, String> request) {
        String token = request.get("verificationToken");
        String email = request.get("email");

        boolean isVerified = userService.verifyUser(token);

        if (isVerified) {
            return ResponseEntity.ok("User verified and registered successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean isReset = userService.resetPassword(token, newPassword);
        if (isReset) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.status(400).body("Invalid reset token");
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userService.updateUser(user);

            sendResetEmail(user);
            return ResponseEntity.ok("Password reset email sent successfully");
        } else {
            return ResponseEntity.status(404).body("Email address not found");
        }
    }

    private void sendResetEmail(User user) {
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + user.getResetToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link: " + resetLink);
        message.setFrom("noreply@example.com");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
