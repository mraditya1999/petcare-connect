package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, UserService userService, EmailService emailService, UserMapper userMapper, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody UserRegistrationRequest userRequest) {
        try {
            User user = userMapper.toEntity(userRequest);
            authService.registerUser(user);
            ApiResponse<String> response = new ApiResponse<>("User registered successfully. Please check your email for the verification link.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistsException e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> loginUser(@Valid @RequestBody UserLoginRequest loginRequest) {
        Optional<User> authenticatedUser = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        return authenticatedUser.map(user -> {
            String token = authService.generateJwtToken(user);
            List<String> roles = user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toList());
            UserLoginResponse userLoginResponse = new UserLoginResponse(user.getEmail(), roles, token, user.getUserId());
            ApiResponse<UserLoginResponse> response = new ApiResponse<>("User logged in successfully", userLoginResponse);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<UserLoginResponse> response = new ApiResponse<>("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        });
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
    public ResponseEntity<ApiResponse<String>> verifyUser(@Valid @RequestBody Map<String, String> request) {
        String token = request.get("verificationToken");
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

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<String>> forgetPassword(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userService.updateResetToken(user);
            emailService.sendResetEmail(user);
            logger.info("Password reset email sent successfully to: {}", email);
            ApiResponse<String> apiResponse = new ApiResponse<>("Password reset email sent successfully");
            return ResponseEntity.ok(apiResponse);
        } else {
            logger.warn("Email address not found: {}", email);
            throw new ResourceNotFoundException("Email address not found");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
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
}
