package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.auth.*;
import com.petconnect.backend.dto.user.GitHubUserDTO;
import com.petconnect.backend.dto.user.GoogleUserDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.*;
import com.petconnect.backend.entity.OAuthAccount;
import com.petconnect.backend.exceptions.IllegalArgumentException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.utils.PhoneUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final com.petconnect.backend.services.RedisStorageService redisStorageService;
    @Value("${github.client.id}")
    private String githubClientIdProp;

    @Value("${github.redirect.uri}")
    private String githubRedirectUriProp;

    @Autowired
    public AuthController(AuthService authService, UserService userService, EmailService emailService, UserRepository userRepository, com.petconnect.backend.services.RedisStorageService redisStorageService) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.redisStorageService = redisStorageService;
    }

    @GetMapping("/github/url")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> getGithubAuthUrl() {
        String state = java.util.UUID.randomUUID().toString();
        java.time.Duration ttl = java.time.Duration.ofMinutes(10);
        redisStorageService.saveOAuthState(state, ttl);

        String clientId = githubClientIdProp != null ? githubClientIdProp : System.getenv("GITHUB_CLIENT_ID");
        String redirectUri = githubRedirectUriProp != null ? githubRedirectUriProp : System.getenv("GITHUB_REDIRECT_URI");

        String url = String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:email&state=%s&allow_signup=true",
            java.net.URLEncoder.encode(clientId, java.nio.charset.StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(redirectUri, java.nio.charset.StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(state, java.nio.charset.StandardCharsets.UTF_8)
        );

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        // include redirectUri so callers can verify exactly which callback is being sent to GitHub
        data.put("redirectUri", redirectUri);
        data.put("state", state);
        logger.info("Generated GitHub auth URL for client_id={} redirectUri={}", clientId, redirectUri);
        return ResponseEntityUtil.ok("OK", data);
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
            authService.registerUser(userRequest);
            UserRegistrationResponseDTO response = new UserRegistrationResponseDTO("User registered successfully. Please check your email for the verification link.");
            return ResponseEntityUtil.created("User registered successfully.", response);
        } catch (UserAlreadyExistsException e) {
            logger.error("User registration failed: ", e);
            return ResponseEntityUtil.conflict(e.getMessage());
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
                boolean isProfileCompleted = user.isProfileComplete();
                String oauthProvider = user.getOauthAccounts().stream()
                        .findFirst()
                        .map(acc -> acc.getProvider().name())
                        .orElse(OAuthAccount.AuthProvider.LOCAL.name());

                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(
                        user.getUserId(),
                        user.getEmail(),
                        roles,
                        token,
                        oauthProvider,
                        isProfileCompleted
                );

                return ResponseEntityUtil.ok("User logged in successfully.", userLoginResponseDTO);
            } else {
                logger.warn("Failed login attempt for email: {}", loginRequest.getEmail());
                return ResponseEntityUtil.unauthorized("Invalid email or password");
            }
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email: {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntityUtil.unauthorized("Invalid email or password");
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
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set expiry to immediate
        response.addCookie(cookie);
        logger.info("User logged out successfully");
        LogoutResponseDTO logoutResponse = new LogoutResponseDTO("User logged out successfully");
        return ResponseEntityUtil.ok("User logged out successfully", logoutResponse);
    }

    /**
     * Verifies a user's email.
     *
     * @param request the request containing the verification token
     * @return the response entity containing the verification status
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<VerifyEmailResponseDTO>> verifyUser(
            @Valid @RequestBody VerifyEmailRequestDTO request) {
        try {
            String token = request.getVerificationToken();
            authService.verifyUser(token); // service handles Redis lookup + deletion

            VerifyEmailResponseDTO body =
                    new VerifyEmailResponseDTO("User verified and registered successfully.", true);

            return ResponseEntityUtil.ok("User verified successfully.", body);
        }  catch (ResourceNotFoundException ex) {

            VerifyEmailResponseDTO body =
                new VerifyEmailResponseDTO(ex.getMessage(), false);

            return ResponseEntityUtil.notFound(ex.getMessage(), body);

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
                return ResponseEntityUtil.ok("Password reset email sent successfully", forgetPasswordResponse);
            } else {
                logger.warn("Email address not found: {}", email);
                throw new ResourceNotFoundException("Email address not found");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Password reset request failed: ", e);
            ForgetPasswordResponseDTO forgetPasswordResponse = new ForgetPasswordResponseDTO(e.getMessage());
            return ResponseEntityUtil.notFound(e.getMessage(), forgetPasswordResponse);
        }
    }

    /**
     * Resets a user's password.
     *
     * @param request the request containing the reset token and new password
     * @return the response entity containing the password reset status
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<ResetPasswordResponseDTO>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        boolean isReset = authService.resetPassword(token, newPassword); // service handles Redis lookup + deletion

        if (isReset) {
            ResetPasswordResponseDTO resetPasswordResponse =
                    new ResetPasswordResponseDTO("Password reset successfully");
            return ResponseEntityUtil.ok("Password reset successfully", resetPasswordResponse);
        } else {
            ResetPasswordResponseDTO resetPasswordResponse =
                    new ResetPasswordResponseDTO("New password cannot be the same as the old password.");
            return ResponseEntityUtil.badRequest("New password cannot be the same as the old password.", resetPasswordResponse);
        }
    }

    /**
     * Authenticates user via Google OAuth.
     *
     * @param body request body containing Google access token
     * @return the response entity with login details and JWT token
     */
    @PostMapping("/google")
        public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> googleLogin(@RequestBody Map<String, String> body) {

            String accessToken = body.get("token");

            try {
                GoogleUserDTO googleUser = authService.fetchGoogleProfile(accessToken);

                // Create / update user
                User user = authService.processGoogleLogin(googleUser, accessToken);

                // Mark Google users as verified
                if (!user.isVerified()) {
                    user.setVerified(true);
                    userRepository.save(user);
                }

                String jwtToken = authService.generateJwtToken(user);

                List<String> roles = user.getRoles().stream()
                        .map(Role::getAuthority)
                        .collect(Collectors.toList());

                String oauthProvider = user.getOauthAccounts().stream()
                        .findFirst()
                        .map(acc -> acc.getProvider().name())
                        .orElse(OAuthAccount.AuthProvider.LOCAL.name());

                boolean isProfileCompleted = user.isProfileComplete();
                UserLoginResponseDTO response = new UserLoginResponseDTO(
                        user.getUserId(),
                        user.getEmail(),
                        roles,
                        jwtToken,
                        oauthProvider,
                        isProfileCompleted
                );

                return ResponseEntityUtil.ok("User logged in successfully.", response);

            } catch (AuthenticationException e) {
                logger.warn("Google login failed: {}", e.getMessage());
                return ResponseEntityUtil.unauthorized("Invalid Google token");
            }
        }
    /**
     * Authenticates user via GitHub OAuth.
     *
     * @param body request body containing GitHub authorization code
     * @return the response entity with login details and JWT token
     */
    @PostMapping("/github")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> githubLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");

        // validate state
        if (state == null || state.isBlank() || redisStorageService.getOAuthState(state) == null) {
            logger.warn("Invalid or missing OAuth state during GitHub login");
            return ResponseEntityUtil.badRequest("Invalid OAuth state");
        }

        try {
            // consume state once
            redisStorageService.deleteOAuthState(state);

            String accessToken = authService.exchangeCodeForAccessToken(code);
            GitHubUserDTO profile = authService.fetchGitHubProfile(accessToken);

            User user = authService.processGitHubLogin(profile, accessToken);

            String jwtToken = authService.generateJwtToken(user);

            List<String> roles = user.getRoles().stream()
                    .map(Role::getAuthority)
                    .collect(Collectors.toList());

            String oauthProvider =
                    user.getOauthAccounts().stream()
                            .anyMatch(acc -> acc.getProvider() == OAuthAccount.AuthProvider.GITHUB)
                            ? OAuthAccount.AuthProvider.GITHUB.name()
                            : OAuthAccount.AuthProvider.LOCAL.name();

            boolean isProfileCompleted = user.isProfileComplete();
            UserLoginResponseDTO response = new UserLoginResponseDTO(
                    user.getUserId(),
                    user.getEmail(),
                    roles,
                    jwtToken,
                    oauthProvider,
                    isProfileCompleted
            );

            return ResponseEntityUtil.ok("User logged in successfully.", response);

        } catch (IllegalStateException e) {
            logger.warn("GitHub exchange failed: {}", e.getMessage());
            return ResponseEntityUtil.badRequest("GitHub OAuth failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during GitHub login", e);
            return ResponseEntityUtil.internalServerError("Internal error during GitHub login");
        }
    }

    /**
     * Sends OTP to the provided phone number.
     *
     * @param body request body containing phone number
     * @return the response entity with status message
     */
    // ---------------- Send OTP ----------------
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> sendOtp(@RequestBody Map<String, String> body) {
        String rawPhone = body.get("phone");
        String phone = PhoneUtils.normalizeToE164(rawPhone);

        if (phone == null) {
            return ResponseEntityUtil.badRequest("Invalid phone format");
        }

        try {
            authService.sendOtp(phone);

            // Returning phone in data for consistency
            Map<String, String> data = Map.of("phone", phone);
            return ResponseEntityUtil.ok("OTP sent successfully", data);
        } catch (IllegalStateException ex) {
            // e.g., cooldown active
            return ResponseEntityUtil.tooManyRequests(ex.getMessage(), Map.of("phone", phone));
        }
    }

    /**
     * Verifies OTP for phone-based authentication.
     *
     * @param body request body containing phone number and OTP code
     * @return the response entity with login details or temp token for new users
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> verifyOtp(@RequestBody Map<String, String> body) {
        String phone = PhoneUtils.normalizeToE164(body.get("phone"));
        String otp = body.get("otp");

        if (phone == null || otp == null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("message", "Phone and OTP required");
            return ResponseEntityUtil.badRequest("Invalid input", errorData);
        }

        try {
            VerifyOtpResult result = authService.verifyOtpAndLogin(phone, otp);

            Map<String, Object> data = new HashMap<>();
            if (result.isNewUser()) {
                data.put("userId", result.getUserId());
                data.put("email", null);
                data.put("roles", List.of());
                data.put("token", null);
                data.put("oauthProvider", null);
                data.put("isProfileComplete", false);
                data.put("isNewUser", true);
                data.put("tempToken", result.getTempToken());
            } else {
                UserLoginResponseDTO loginResponse = result.getLoginResponse();
                data.put("userId", loginResponse.getUserId());
                data.put("email", loginResponse.getEmail());
                data.put("roles", loginResponse.getRoles());
                data.put("token", loginResponse.getToken());
                data.put("oauthProvider", loginResponse.getOauthProvider());
                data.put("isProfileComplete", loginResponse.isProfileComplete());
                data.put("isNewUser", false);
                data.put("tempToken", null);
            }

            String message = result.isNewUser() ? "New user. Complete profile." : "User logged in successfully.";
            return ResponseEntityUtil.ok(message, data);

        } catch (InvalidOtpException ex) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("message", ex.getMessage());
            return ResponseEntityUtil.unauthorized("Invalid OTP", errorData);

        } catch (OtpExpiredException ex) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("message", ex.getMessage());
            return ResponseEntityUtil.conflict("OTP expired", errorData);

        } catch (UserNotFoundException ex) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("message", ex.getMessage());
            return ResponseEntityUtil.notFound("User not found", errorData);
        }
    }

    /**
     * Completes user profile for phone-authenticated new users.
     *
     * @param dto the profile completion request containing email, name, and phone
     * @param request the HTTP request to extract bearer token
     * @return the response entity with complete login details
     */
    @PostMapping("/complete-profile")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> completeProfile(
            @RequestBody @Valid CompleteProfileDTO dto,
            HttpServletRequest request) {

        // Extract Bearer token
        String token = extractBearerToken(request);
        String normalizedPhone = PhoneUtils.normalizeToE164(dto.getPhone());

        if (token == null || !authService.validateTempTokenForPhone(token, normalizedPhone)) {
            return ResponseEntityUtil.unauthorized("Invalid or expired token");
        }

        try {
            // Complete the profile and get the login response
            UserLoginResponseDTO loginResponse = authService.completeProfile(dto);

            // Put all fields directly into the data map
            Map<String, Object> data = new HashMap<>();
            data.put("userId", loginResponse.getUserId());
            data.put("email", loginResponse.getEmail());
            data.put("roles", loginResponse.getRoles());
            data.put("token", loginResponse.getToken());
            data.put("oauthProvider", loginResponse.getOauthProvider());
            data.put("isProfileComplete", loginResponse.isProfileComplete());
            data.put("isNewUser", false);   // profile completed
            data.put("tempToken", null);    // temp token no longer needed

            return ResponseEntityUtil.ok("Profile completed successfully.", data);

        } catch (IllegalArgumentException ex) {
            return ResponseEntityUtil.badRequest(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntityUtil.conflict("Data integrity error (possible duplicate email)");
        } catch (Exception ex) {
            logger.error("complete-profile error", ex);
            return ResponseEntityUtil.internalServerError("Server error");
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
