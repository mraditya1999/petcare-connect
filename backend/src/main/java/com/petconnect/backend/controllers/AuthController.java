package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.auth.*;
import com.petconnect.backend.dto.auth.CompleteProfileRequestDTO;
import com.petconnect.backend.dto.auth.GitHubUserDTO;
import com.petconnect.backend.dto.auth.GoogleUserDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.*;
import com.petconnect.backend.entity.OAuthAccount;
import com.petconnect.backend.exceptions.IllegalArgumentException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.UserService;
import com.petconnect.backend.utils.CommonUtils;
import com.petconnect.backend.utils.PhoneUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Authentication",
        description = "Authentication, OAuth, OTP and profile management APIs"
)
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

    /**
     * Registers a new user.
     *
     * @param request the user registration request
     * @return the response entity containing the registration status
     */
    @Operation(summary = "Register new user", description = "Registers user and sends verification email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserRegistrationResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO request
    ) {
        try {
            authService.registerUser(request);
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
     * @param request the user login request
     * @return the response entity containing the login status and user details
     */
    @Operation(summary = "Login with email & password", description = "Authenticates user and returns JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> login(
            @Valid @RequestBody UserLoginRequestDTO request
    ) {
        logger.info("Login attempt for email: {}", request.getEmail());
        try {
            Optional<User> authenticatedUser = authService.authenticateUser(request.getEmail(), request.getPassword());
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
                logger.warn("Failed login attempt for email: {}", request.getEmail());
                return ResponseEntityUtil.unauthorized("Invalid email or password");
            }
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntityUtil.unauthorized("Invalid email or password");
        }
    }

    /**
     * Logs out a user.
     *
     * @param response the HTTP response
     * @return the response entity containing the logout status
     */
    @Operation(summary = "Logout user")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponseDTO<UserLogoutResponseDTO>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.info("User logged out successfully");
        UserLogoutResponseDTO logoutResponse = new UserLogoutResponseDTO("User logged out successfully");
        return ResponseEntityUtil.ok("User logged out successfully", logoutResponse);
    }

    /**
     * Verifies a user's email.
     *
     * @param request the request containing the verification token
     * @return the response entity containing the verification status
     */
    @Operation(summary = "Verify email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified"),
            @ApiResponse(responseCode = "404", description = "Invalid token")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO<VerifyEmailResponseDTO>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDTO request
    ) {
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
    @Operation(summary = "Forgot password")
    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponseDTO<ForgetPasswordResponseDTO>> forgotPassword(
            @Valid @RequestBody ForgetPasswordRequestDTO request
    ) {
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
    @Operation(summary = "Reset password")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<ResetPasswordResponseDTO>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
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
    @Operation(summary = "Login with Google")
    @PostMapping("/google")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> googleLogin(
            @RequestBody Map<String, String> body
    ) {

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
                UserLoginResponseDTO response = new UserLoginResponseDTO(user.getUserId(),user.getEmail(),roles,jwtToken,oauthProvider,isProfileCompleted);

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
    @Operation(summary = "Login with GitHub")
    @PostMapping("/github")
    public ResponseEntity<ApiResponseDTO<UserLoginResponseDTO>> githubLogin(
            @RequestBody Map<String, String> body
    ) {
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
            UserLoginResponseDTO response = new UserLoginResponseDTO(user.getUserId(),user.getEmail(),roles,jwtToken,oauthProvider,isProfileCompleted);

            return ResponseEntityUtil.ok("User logged in successfully.", response);

        } catch (IllegalStateException e) {
            logger.warn("GitHub exchange failed: {}", e.getMessage());
            return ResponseEntityUtil.badRequest("GitHub OAuth failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during GitHub login", e);
            return ResponseEntityUtil.internalServerError("Internal error during GitHub login");
        }
    }

    @Hidden
    @GetMapping("/github/url")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> githubUrl() {
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
        data.put("redirectUri", redirectUri);
        data.put("state", state);
        logger.info("Generated GitHub auth URL for client_id={} redirectUri={}", clientId, redirectUri);
        return ResponseEntityUtil.ok("OK", data);
    }
    /**
     * Sends OTP to the provided phone number.
     *
     * @param request request body containing phone number
     * @return the response entity with status message
     */
    @Operation(summary = "Send OTP")
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponseDTO<OtpLoginResponseDTO>> sendOtp(
            @Valid @RequestBody OtpLoginRequestDTO request
    ) {
        String rawPhone = request.getPhone();
        String phone = PhoneUtils.normalizeToE164(rawPhone);

        if (phone == null) {
            return ResponseEntityUtil.badRequest("Invalid phone format");
        }

        try {
            authService.sendOtp(phone);
            OtpLoginResponseDTO otpLoginResponseDTO = new OtpLoginResponseDTO(phone);
            return ResponseEntityUtil.ok("OTP sent successfully", otpLoginResponseDTO);
        } catch (IllegalStateException ex) {
            OtpLoginResponseDTO otpLoginResponseDTO = new OtpLoginResponseDTO(phone);
            return ResponseEntityUtil.tooManyRequests(ex.getMessage(), otpLoginResponseDTO);
        }
    }

    /**
     * Verifies OTP for phone-based authentication.
     *
     * @param request request body containing phone number and OTP code
     * @return the response entity with login details or temp token for new users
     */
    @Operation(summary = "Verify OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<VerifyOtpResponseDTO>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequestDTO request
    ) {

        String rawPhone = request.getPhone();
        String phone = PhoneUtils.normalizeToE164(rawPhone);
        String otp = request.getOtp();

        if (phone == null || otp == null) {
            return ResponseEntityUtil.badRequest(
                    "Invalid input",
                    null // or you could return a minimal DTO with just isNewUser=false
            );
        }

        try {
            VerifyOtpResponseDTO result = authService.verifyOtpAndLogin(phone, otp);

            String message = result.isNewUser()
                    ? "New user. Complete profile."
                    : "User logged in successfully.";

            return ResponseEntityUtil.ok(message, result);

        } catch (InvalidOtpException ex) {
            return ResponseEntityUtil.unauthorized("Invalid OTP",null );
        } catch (OtpExpiredException ex) {
            return ResponseEntityUtil.conflict("OTP expired",null);
        } catch (UserNotFoundException ex) {
            return ResponseEntityUtil.notFound("User not found",null);
        }
    }

    /**
     * Completes user profile for phone-authenticated new users.
     *
     * @param request the profile completion request containing email, name, and phone
     * @param request the HTTP request to extract bearer token
     * @return the response entity with complete login details
     */
    @Operation(summary = "Complete profile for OTP users")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/complete-profile")
    public ResponseEntity<ApiResponseDTO<CompleteProfileResponseDTO>> completeProfile(
            @Valid @RequestBody CompleteProfileRequestDTO request,
            HttpServletRequest http
    ) {

        String token = CommonUtils.extractBearerToken(http);
        String normalizedPhone = PhoneUtils.normalizeToE164(request.getPhone());

        if (token == null || !authService.validateTempTokenForPhone(token, normalizedPhone)) {
            return ResponseEntityUtil.unauthorized("Invalid or expired token");
        }

        try {
            UserLoginResponseDTO userLoginResponseDTO = authService.completeProfile(request);
            CompleteProfileResponseDTO responseDto = new CompleteProfileResponseDTO(
                    userLoginResponseDTO.getEmail(),
                    userLoginResponseDTO.getRoles(),
                    userLoginResponseDTO.getToken(),
                    userLoginResponseDTO.getUserId(),
                    userLoginResponseDTO.getOauthProvider(),
                    false // profile completed, not a new user anymore
            );

            return ResponseEntityUtil.ok("Profile completed successfully.", responseDto);

        } catch (IllegalArgumentException ex) {
            return ResponseEntityUtil.badRequest(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntityUtil.conflict("Data integrity error (possible duplicate email)");
        } catch (Exception ex) {
            logger.error("complete-profile error", ex);
            return ResponseEntityUtil.internalServerError("Server error");
        }
    }
}
