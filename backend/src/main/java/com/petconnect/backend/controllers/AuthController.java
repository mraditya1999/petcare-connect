package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.UserLoginRequest;
import com.petconnect.backend.dto.UserLoginResponse;
import com.petconnect.backend.dto.UserRegistrationRequest;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRegistrationRequest userRequest) {
        try {
            String email = userRequest.getEmail();

            if (userService.existsByEmail(email)) {
                ApiResponse<Void> response = new ApiResponse<>("User already exists with this email.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            User user = new User();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(email);
            user.setPassword(userRequest.getPassword());
            userService.registerUser(user);

            ApiResponse<Void> response = new ApiResponse<>("User registered successfully. Please check your email for the verification link.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> loginUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            Optional<User> authenticatedUser = userService.authenticateUser(email, password);
            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();
                String token = userService.generateJwtToken(user);

                UserLoginResponse userLoginResponse = new UserLoginResponse(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRoles().stream().map(Role::getRoleName).findFirst().orElse("user"),
                        token
                );

                ApiResponse<UserLoginResponse> response = new ApiResponse<>("User logged in successfully", userLoginResponse);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<UserLoginResponse> response = new ApiResponse<>("Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            ApiResponse<UserLoginResponse> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean isReset = userService.resetPassword(token, newPassword);
        if (isReset) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            throw new ResourceNotFoundException("Invalid reset token");
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
            emailService.sendResetEmail(user);
            return ResponseEntity.ok("Password reset email sent successfully");
        } else {
            throw new ResourceNotFoundException("Email address not found");
        }
    }
}
