package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.auth.*;
import com.petconnect.backend.dto.user.GoogleUserDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.EmailService;
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
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService, UserService userService, EmailService emailService, UserMapper userMapper, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     *
     * @param userRequest the user registration request
     * @return the response entity containing the registration status
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserRegistrationResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationRequestDTO userRequest) {
        try {
            User user = userMapper.toEntity(userRequest);
            authService.registerUser(user);
            UserRegistrationResponseDTO response = new UserRegistrationResponseDTO("User registered successfully. Please check your email for the verification link.");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>("User registered successfully.",response));
        } catch (UserAlreadyExistsException e) {
            logger.error("User registration failed: ", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO<>(e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal server error during registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Logs in a user.
     *
     * @param loginRequest the user login request
     * @return the response entity containing the login status and user details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> loginUser(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            Optional<User> authenticatedUser = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();
                String token = authService.generateJwtToken(user);
                List<String> roles = user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toList());
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(user.getEmail(), roles, token, user.getUserId(),user.getOauthProvider().name());
                return ResponseEntity.ok(new ApiResponseDTO<>("User logged in successfully.", userLoginResponseDTO));
            } else {
                logger.warn("Failed login attempt for email: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO<>("Invalid email or password"));
            }
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email: {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO<>("Invalid email or password"));
        } catch (Exception e) {
            logger.error("Internal server error during login: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Logs out a user.
     *
     * @param response the HTTP response
     * @return the response entity containing the logout status
     */
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponseDTO<LogoutResponseDTO>> logoutUser(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // Set expiry to immediate
            response.addCookie(cookie);
            logger.info("User logged out successfully");
            LogoutResponseDTO logoutResponse = new LogoutResponseDTO("User logged out successfully");
            return ResponseEntity.ok(new ApiResponseDTO<>("User logged out successfully", logoutResponse));
        } catch (Exception e) {
            logger.error("Internal server error during logout: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null));
        }
    }

    /**
     * Verifies a user's email.
     *
     * @param request the request containing the verification token
     * @return the response entity containing the verification status
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<VerifyEmailResponseDTO>> verifyUser(@Valid @RequestBody VerifyEmailRequestDTO request) {
        try {
            String token = request.getVerificationToken();
            boolean isVerified = authService.verifyUser(token);
            if (isVerified) {
                logger.info("User verified successfully with token: {}", token);
                VerifyEmailResponseDTO verifyEmailResponse = new VerifyEmailResponseDTO("User verified and registered successfully.", true);
                return ResponseEntity.ok(new ApiResponseDTO<>("User verified successfully.", verifyEmailResponse));
            } else {
                logger.warn("Invalid or expired verification token: {}", token);
                throw new ResourceNotFoundException("Invalid or expired verification token.");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Email verification failed: ", e);
            VerifyEmailResponseDTO verifyEmailResponse = new VerifyEmailResponseDTO(e.getMessage(), false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>(e.getMessage(), verifyEmailResponse));
        } catch (Exception e) {
            logger.error("Internal server error during email verification: ", e);
            VerifyEmailResponseDTO verifyEmailResponse = new VerifyEmailResponseDTO("An error occurred: " + e.getMessage(), false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), verifyEmailResponse));
        }
    }

    /**
     * Initiates a password reset process.
     *
     * @param request the request containing the user's email
     * @return the response entity containing the password reset status
     */
    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponseDTO<ForgetPasswordResponseDTO>> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDTO request) {
        try {
            String email = request.getEmail();
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String resetToken = UUID.randomUUID().toString();
                user.setResetToken(resetToken);
                userService.updateResetToken(user);
                emailService.sendResetEmail(user);
                logger.info("Password reset email sent successfully to: {}", email);
                ForgetPasswordResponseDTO forgetPasswordResponse = new ForgetPasswordResponseDTO("Password reset email sent successfully");
                return ResponseEntity.ok(new ApiResponseDTO<>("Password reset email sent successfully", forgetPasswordResponse));
            } else {
                logger.warn("Email address not found: {}", email);
                throw new ResourceNotFoundException("Email address not found");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Password reset request failed: ", e);
            ForgetPasswordResponseDTO forgetPasswordResponse = new ForgetPasswordResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>(e.getMessage(), forgetPasswordResponse));
        } catch (Exception e) {
            logger.error("Internal server error during password reset request: ", e);
            ForgetPasswordResponseDTO forgetPasswordResponse = new ForgetPasswordResponseDTO("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), forgetPasswordResponse));
        }
    }

    /**
     * Resets a user's password.
     *
     * @param request the request containing the reset token and new password
     * @return the response entity containing the password reset status
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<ResetPasswordResponseDTO>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        try {
            String token = request.getToken();
            String newPassword = request.getNewPassword();

            logger.info("Received reset request for token: {}", token);

            boolean isReset = authService.resetPassword(token, newPassword);

            if (isReset) {
                logger.info("Password reset successfully with token: {}", token);
                ResetPasswordResponseDTO resetPasswordResponse = new ResetPasswordResponseDTO("Password reset successfully");
                return ResponseEntity.ok(new ApiResponseDTO<>("Password reset successfully", resetPasswordResponse));
            } else {
                logger.warn("Password reset failed: New password cannot be the same as the old password.");
                ResetPasswordResponseDTO resetPasswordResponse = new ResetPasswordResponseDTO("New password cannot be the same as the old password.");
                return ResponseEntity.badRequest().body(new ApiResponseDTO<>("New password cannot be the same as the old password.", resetPasswordResponse));
            }
        } catch (Exception e) {
            logger.error("Internal server error during password reset: ", e);
            ResetPasswordResponseDTO resetPasswordResponse = new ResetPasswordResponseDTO("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), resetPasswordResponse));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> googleLogin(@RequestBody Map<String, String> body) {
        String accessToken = body.get("token");

        try {
            GoogleUserDTO googleUser = authService.fetchGoogleProfile(accessToken);
            User user = authService.processGoogleLogin(googleUser);

            String token = authService.generateJwtToken(user);
            List<String> roles = user.getRoles().stream()
                    .map(Role::getAuthority)
                    .collect(Collectors.toList());

            UserLoginResponseDTO userLoginResponseDTO =
                    new UserLoginResponseDTO(user.getEmail(), roles, token, user.getUserId(),user.getOauthProvider().name());

            return ResponseEntity.ok(new ApiResponseDTO<>("User logged in successfully.", userLoginResponseDTO));
        } catch (AuthenticationException e) {
            logger.warn("Google authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>("Invalid Google token"));
        } catch (Exception e) {
            logger.error("Internal server error during Google login: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>("An error occurred: " + e.getMessage()));
        }
    }

}
