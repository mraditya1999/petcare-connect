package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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

    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegistrationResponse>> registerUser(@RequestBody UserRegistrationRequest userRequest) {
        try {
            User user = new User();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            authService.registerUser(user);

            ApiResponse<UserRegistrationResponse> response = new ApiResponse<>("User registered successfully. Please check your email for the verification link.");
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            ApiResponse<UserRegistrationResponse> response = new ApiResponse<>(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            ApiResponse<UserRegistrationResponse> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> loginUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            Optional<User> authenticatedUser = authService.authenticateUser(email, password);
            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();
                String token = authService.generateJwtToken(user);

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
        } catch (AuthenticationException e) {
            ApiResponse<UserLoginResponse> response = new ApiResponse<>(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<UserLoginResponse> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set expiry to immediate
        response.addCookie(cookie);

        logger.info("User logged out successfully");
        ApiResponse<String> apiResponse = new ApiResponse<>("User logged out successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyUser(@Validated @RequestBody Map<String, String> request) {
        String token = request.get("verificationToken");
        String email = request.get("email");

        boolean isVerified = authService.verifyUser(token);
        if (isVerified) {
            logger.info("User verified successfully with token: {}", token);
            ApiResponse<String> apiResponse = new ApiResponse<>("User verified and registered successfully.");
            return ResponseEntity.ok(apiResponse);
        } else {
            logger.warn("Invalid or expired verification token: {}", token);
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean isReset = authService.resetPassword(token, newPassword);
        if (isReset) {
            logger.info("Password reset successfully with token: {}", token);
            ApiResponse<String> apiResponse = new ApiResponse<>("Password reset successfully");
            return ResponseEntity.ok(apiResponse);
        } else {
            logger.warn("Invalid reset token: {}", token);
            throw new ResourceNotFoundException("Invalid reset token");
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<String>> forgetPassword(@Validated @RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userService.updateUser(user);
            emailService.sendResetEmail(user);
            logger.info("Password reset email sent successfully to: {}", email);
            ApiResponse<String> apiResponse = new ApiResponse<>("Password reset email sent successfully");
            return ResponseEntity.ok(apiResponse);
        } else {
            logger.warn("Email address not found: {}", email);
            throw new ResourceNotFoundException("Email address not found");
        }
    }
}
