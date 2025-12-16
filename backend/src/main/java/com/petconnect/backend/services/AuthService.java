package com.petconnect.backend.services;

import com.petconnect.backend.dto.CompleteProfileDTO;
import com.petconnect.backend.dto.TempUserDTO;
import com.petconnect.backend.dto.VerifyOtpResult;
import com.petconnect.backend.dto.auth.UserLoginResponseDTO;
import com.petconnect.backend.dto.auth.UserRegistrationRequestDTO;
import com.petconnect.backend.dto.user.GitHubUserDTO;
import com.petconnect.backend.dto.user.GoogleUserDTO;
import com.petconnect.backend.dto.user.OAuthProfile;
import com.petconnect.backend.entity.OAuthAccount;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ApiException;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.TempUserMapper;
import com.petconnect.backend.mappers.UserMapper;
import com.petconnect.backend.repositories.OAuthAccountRepository;
import com.petconnect.backend.utils.CommonUtils;
import com.petconnect.backend.utils.PhoneUtils;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.security.JwtUtil;
import com.petconnect.backend.security.UserDetailsServiceImpl;
import com.petconnect.backend.utils.TempUserStore;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.core.ParameterizedTypeReference;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final long OAUTH_TOKEN_EXPIRY_SECONDS = 3600L;
    private static final long TEMP_TOKEN_TTL_MS = 1000L * 60 * 10;

        // Password strength: at least 8 chars, 1 upper, 1 lower, 1 digit, 1 special
        private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$"
        );

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.ttlMinutes:5}")
    private int otpTtlMinutes;

    @Value("${otp.max.verify.attempts:5}")
    private int maxVerifyAttempts;

    @Value("${otp.resendCooldownSeconds:30}")
    private int resendCooldownSeconds;

    @Value("${otp.block.seconds:3600}")  // default 1 hour
    private long blockSeconds;

    @Value("${verification.token.ttl.hours:24}")
    private long verificationTtlHours;

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final JwtUtil jwtUtil;
    private final TempUserStore tempUserStore;
    private final OAuthAccountRepository oauthAccountRepository;
    private final OtpRedisService otpRedisService;
    private final SmsSender smsSender;
    private final RedisStorageService redisStorageService;
    private final UserMapper userMapper;
    private final TempUserMapper tempUserMapper;


    @Autowired
    public AuthService(WebClient webClient, UserRepository userRepository,
                       RoleAssignmentUtil roleAssignmentUtil,
                       @Lazy PasswordEncoder passwordEncoder,
                       @Lazy VerificationService verificationService,
                       JwtUtil jwtUtil,
                       TempUserStore tempUserStore,
                       OAuthAccountRepository oauthAccountRepository, OtpRedisService otpRedisService, SmsSender smsSender, RedisStorageService redisStorageService, UserMapper userMapper, TempUserMapper tempUserMapper) {
        this.webClient = webClient;
        this.userRepository = userRepository;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.jwtUtil = jwtUtil;
        this.tempUserStore = tempUserStore;
        this.oauthAccountRepository = oauthAccountRepository;
        this.otpRedisService = otpRedisService;
        this.smsSender = smsSender;
        this.redisStorageService = redisStorageService;
        this.userMapper = userMapper;
        this.tempUserMapper = tempUserMapper;
    }

    /**
     * Loads the user by username (email) for authentication.
     *
     * @param email the user's email
     * @return the UserDetails object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email cannot be null or blank");
        }
        
        try {
            email = email.toLowerCase(Locale.ROOT);
            String finalEmail = email;
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + finalEmail));

            Set<GrantedAuthority> authorities = user.getRoles() != null ? user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                    .collect(Collectors.toSet()) : new HashSet<>();

            // If password is null (OAuth-only account), supply empty string to avoid NPE in Spring User constructor.
            String pwd = user.getPassword() == null ? "" : user.getPassword();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), pwd, authorities);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", email, e);
            throw new UsernameNotFoundException("Error loading user: " + email, e);
        }
    }

    /**
     * Registers a new user.
     *
     * @param dto the user dto to register
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Transactional
    public void registerUser(UserRegistrationRequestDTO  dto) {
        String email = dto.getEmail().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        User user = userMapper.toEntity(dto);

        user.setEmail(email);
        String rawPassword = user.getPassword();
        if (rawPassword == null || !PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include uppercase, lowercase, digit and special character.");
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setVerified(false);

        boolean isFirstVerifiedUser  = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser );
        roleAssignmentUtil.assignRoles(user, roles);

        String token = CommonUtils.generateSecureToken();
        user.setVerificationToken(token);

        // Save user to database with unverified status
        userRepository.save(user);

        TempUserDTO tempUserDTO = tempUserMapper.toTempUserDTO(user);
        Duration ttl = Duration.ofHours(verificationTtlHours);
        tempUserStore.saveTemporaryUser(token, tempUserDTO,ttl);
        redisStorageService.saveVerificationToken(token,email, ttl);

        verificationService.sendVerificationEmail(user);
        logger.info("User registered with email: {}", user.getEmail());
    }

    /**
     * Authenticates a user.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return an Optional containing the authenticated user, if found
     * @throws AuthenticationException if authentication fails
     */
    /**
     * Authenticates a user with email and password.
     *
     * @param email the user's email (must not be null or blank)
     * @param password the user's password (must not be null or blank)
     * @return an Optional containing the authenticated user
     * @throws IllegalArgumentException if email or password is null/blank
     * @throws AuthenticationException if authentication fails
     */
    public Optional<User> authenticateUser(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        
        try {
            final String normalizedEmail = email.toLowerCase(Locale.ROOT).trim();
            User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> {
                        logger.warn("Authentication attempt with non-existent email: {}", normalizedEmail);
                        return new AuthenticationException("Invalid email or password.");
                    });

            if (user.getPassword() == null) {
                // This account does not have a password (likely OAuth-only)
                logger.warn("Attempt to password-authenticate OAuth-only account: {}", email);
                throw new AuthenticationException("Invalid email or password.");
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("User authenticated with email: {}", email);
                return Optional.of(user);
            } else {
                logger.warn("Invalid password for email: {}", email);
                throw new AuthenticationException("Invalid email or password.");
            }
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error authenticating user with email: {}", email, e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Generates a JWT token for an authenticated user.
     *
     * @param user the authenticated user
     * @return the generated JWT token
     */
    public String generateJwtToken(User user) {
        UserDetails userDetails = new UserDetailsServiceImpl(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::getAuthority).toArray());
        String token = jwtUtil.generateToken(claims, userDetails.getUsername());
        logger.info("JWT token generated for user with email: {}", user.getEmail());
        return token;
    }

    /**
     * Verifies a user using a verification token.
     *
     * @param verificationToken the verification token
     * @return true if the user is verified, false otherwise
     */
    public boolean verifyUser(String verificationToken) {
        return verificationService.verifyUser(verificationToken);
    }

    /**
     * Resets a user's password using a reset token.
     *
     * @param resetToken  the reset token
     * @param newPassword the new password
     * @return true if the password is reset, false otherwise
     */
    public boolean resetPassword(String resetToken, String newPassword) {
        return verificationService.resetPassword(resetToken, newPassword);
    }

    public GoogleUserDTO fetchGoogleProfile(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserDTO.class)
                .doOnError(error -> logger.error("Error fetching Google profile: {}", error.getMessage()))
                .block(); // Blocking call since this is used in a non-reactive context
    }

    /**
     * Scalable, robust Google login handler:
     *  - prefer provider lookup (provider + providerUserId)
     *  - fallback to email matching (linking) if provider record not found
     *  - create user if not exist
     *  - create OAuthAccount only when missing
     */
    // ---------------------------
    // Provider-specific adapters
    // ---------------------------

    @Transactional
    public User processGoogleLogin(GoogleUserDTO profile, String rawAccessToken) {
        if (profile == null) throw new IllegalArgumentException("Google profile is null");
        OAuthProfile p = OAuthProfile.fromGoogle(profile);
        return processOAuthLoginGeneric(OAuthAccount.AuthProvider.GOOGLE, p, rawAccessToken);
    }

    @Transactional
    public User processGitHubLogin(GitHubUserDTO profile, String rawAccessToken) {
        if (profile == null) throw new IllegalArgumentException("GitHub profile is null");
        OAuthProfile p = OAuthProfile.fromGitHub(profile);
        return processOAuthLoginGeneric(OAuthAccount.AuthProvider.GITHUB, p, rawAccessToken);
    }

    // ---------------------------
    // Generic provider flow
    // ---------------------------

    /**
     * Generic oauth login flow used by Google, Github, etc.
     * - prefers existing provider account
     * - falls back to email linking
     * - creates new user when necessary
     * - ensures a non-empty password is set to satisfy validation
     * - creates OAuthAccount if missing
     */
    @Transactional
    public User processOAuthLoginGeneric(OAuthAccount.AuthProvider provider, OAuthProfile profile, String rawAccessToken) {
        if (profile.getProviderUserId() == null || profile.getEmail() == null) {
            throw new IllegalArgumentException("Invalid profile: missing providerUserId or email");
        }

        final String providerUserId = profile.getProviderUserId();
        final String email = profile.getEmail().toLowerCase(Locale.ROOT);

        logger.debug("Processing OAuth login: provider={}, providerUserId={}, email={}", provider, providerUserId, email);

        // 1) provider lookup (exact match)
        Optional<OAuthAccount> existingAccountOpt =
                oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);

        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            User existingUser = existingAccount.getUser();
            boolean changed = false;

            // refresh token if provided
            if (rawAccessToken != null && !rawAccessToken.isBlank()) {
                existingAccount.setAccessToken(rawAccessToken);
                existingAccount.setTokenExpiry(Instant.now().plusSeconds(OAUTH_TOKEN_EXPIRY_SECONDS));
                changed = true;
            }

            changed |= updateUserFromProfileIfNeeded(existingUser, profile);

            if (changed) {
                oauthAccountRepository.save(existingAccount);
                userRepository.save(existingUser);
                logger.debug("Updated existing user {} from provider {}", existingUser.getEmail(), provider);
            }
            return existingUser;
        }

        // 2) fallback by email
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // SECURITY: ensure user doesn't already have another account for this provider with a different providerUserId
            oauthAccountRepository.findByUser(user).forEach(acc -> {
                if (acc.getProvider() == provider && !acc.getProviderUserId().equals(providerUserId)) {
                    String msg = "Email already linked with another " + provider + " account.";
                    logger.warn("{} userEmail={}, existingProviderUserId={}, newProviderUserId={}", msg, email, acc.getProviderUserId(), providerUserId);
                    throw new IllegalStateException(msg);
                }
            });

            boolean changed = updateUserFromProfileIfNeeded(user, profile);
            if (!user.isVerified()) {
                user.setVerified(true);
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
                logger.debug("Linked existing user {} with provider data", user.getEmail());
            }
        } else {
            // 3) create new user
            user = createUserFromProfile(profile);
            logger.debug("Created new user {} for provider {}", user.getEmail(), provider);
        }

        // 4) create OAuth account if missing
        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.setUser(user);
        oauthAccount.setProvider(provider);
        oauthAccount.setProviderUserId(providerUserId);

        if (rawAccessToken != null && !rawAccessToken.isBlank()) {
            oauthAccount.setAccessToken(rawAccessToken);
            oauthAccount.setTokenExpiry(Instant.now().plusSeconds(OAUTH_TOKEN_EXPIRY_SECONDS));
        }

        oauthAccountRepository.save(oauthAccount);
        user.getOauthAccounts().add(oauthAccount);

        // save user if not already
        userRepository.save(user);

        return user;
    }

    // ---------------------------
    // Helper methods
    // ---------------------------

    private User createUserFromProfile(OAuthProfile profile) {
        User user = new User();
        user.setEmail(profile.getEmail().toLowerCase(Locale.ROOT));
        user.setFirstName(profile.getFirstName() != null ? profile.getFirstName() : "User");
        user.setLastName(profile.getLastName() != null ? profile.getLastName() : "User");
        user.setAvatarUrl(profile.getAvatarUrl());
        user.setVerified(true);

        // Make sure validation won't fail (non-empty password)
        String randomPassword = CommonUtils.generateSecureRandomPassword();
        user.setPassword(passwordEncoder.encode(randomPassword));

        boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
        roleAssignmentUtil.assignRoles(user, roles);

        return userRepository.save(user);
    }

    /**
     * Update user's public profile fields from the provider profile if they differ.
     * Returns true if user was changed.
     */
    private boolean updateUserFromProfileIfNeeded(User user, OAuthProfile profile) {
        boolean changed = false;
        if (profile.getFirstName() != null && !profile.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(profile.getFirstName());
            changed = true;
        }
        if (profile.getLastName() != null && !profile.getLastName().equals(user.getLastName())) {
            user.setLastName(profile.getLastName());
            changed = true;
        }
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(profile.getAvatarUrl());
            changed = true;
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            String randomPassword = CommonUtils.generateSecureRandomPassword();
            user.setPassword(passwordEncoder.encode(randomPassword));
            changed = true;
        }
        return changed;
    }



    // ---------------------------
    // Access token exchange and profile fetch for GitHub
    // (kept minimal here – you already have working methods)
    // ---------------------------

    public String exchangeCodeForAccessToken(String code) {
        String url = "https://github.com/login/oauth/access_token";

        // Validate GitHub credentials are configured
        if (githubClientId == null || githubClientId.isBlank() ||
            githubClientSecret == null || githubClientSecret.isBlank() ||
            githubRedirectUri == null || githubRedirectUri.isBlank()) {
            logger.error("GitHub OAuth credentials not properly configured");
            throw new IllegalStateException("GitHub OAuth credentials not configured");
        }

        if (code == null || code.isBlank()) {
            logger.error("GitHub authorization code is null or empty");
            throw new IllegalArgumentException("GitHub authorization code is required");
        }

        org.springframework.util.MultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("client_id", githubClientId);
        body.add("client_secret", githubClientSecret);
        body.add("code", code);
        body.add("redirect_uri", githubRedirectUri);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(body))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .doOnError(error -> logger.error("GitHub OAuth error: {}", error.getMessage()))
                    .block(); // Blocking call since this is used in a non-reactive context
            
            if (response == null) {
                logger.error("Empty body returned from GitHub token exchange");
                throw new IllegalStateException("Failed to exchange code for access token");
            }

            Object token = response.get("access_token");
            if (token == null) {
                logger.error("No access_token in response from GitHub. Full response: {}", response);
                throw new IllegalStateException("No access token returned from GitHub");
            }

            logger.info("Successfully exchanged GitHub code for access token");
            return token.toString();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            logger.error("GitHub OAuth error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new IllegalStateException("Failed to exchange code with GitHub: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error during GitHub token exchange: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Failed to exchange code with GitHub: " + ex.getMessage(), ex);
        }
    }

    public GitHubUserDTO fetchGitHubProfile(String accessToken) {
        String url = "https://api.github.com/user";

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GitHubUserDTO.class)
                .doOnError(error -> logger.error("Error fetching GitHub profile: {}", error.getMessage()))
                .block(); // Blocking call since this is used in a non-reactive context
    }

    private String generateNumericOtp(int len) {
        SecureRandom r = new SecureRandom();
        int min = (int) Math.pow(10, len - 1);
        int max = (int) Math.pow(10, len) - 1;
        int val = r.nextInt((max - min) + 1) + min;
        return String.format("%0" + len + "d", val);
    }

    /**
     * Sends an OTP to the specified phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @throws IllegalArgumentException if phone is null or blank
     * @throws IllegalStateException if phone is in cooldown or blocked
     */
    public void sendOtp(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }

        try {
            String normalizedPhone = PhoneUtils.normalizeToE164(phone);
            if (normalizedPhone == null) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            if (otpRedisService.isPhoneBlocked(normalizedPhone)) {
                logger.warn("OTP send attempt for blocked phone: {}", normalizedPhone);
                throw new IllegalStateException("Phone number is temporarily blocked. Please try again later.");
            }

            if (otpRedisService.isInCooldown(normalizedPhone)) {
                logger.warn("OTP send attempt during cooldown for phone: {}", normalizedPhone);
                throw new IllegalStateException("Please wait before requesting another code.");
            }

            String otp = generateNumericOtp(otpLength);
            String hashed = passwordEncoder.encode(otp);

            otpRedisService.saveOtp(normalizedPhone, hashed);  // stores OTP hash in Redis
            otpRedisService.setCooldown(normalizedPhone);       // sets cooldown

            String message = String.format("Your verification code is %s. Expires in %d minutes.", otp, otpTtlMinutes);
            smsSender.sendSms(normalizedPhone, message);

            // Log OTP sent (without exposing code)
            logger.info("OTP sent to phone: {}", normalizedPhone);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error sending OTP to phone: {}", phone, e);
            throw new ApiException(
                    "Failed to send OTP: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "OTP_SEND_ERROR",
                    e
            );
        }
    }



    @Transactional
    public VerifyOtpResult verifyOtpAndLogin(String phone, String otp) {
        String normalizedPhone = PhoneUtils.normalizeToE164(phone);
        if (normalizedPhone == null)
            throw new IllegalArgumentException("Invalid phone number format");

        // -----------------------------------------------------
        // 🔐 1. CHECK IF PHONE IS BLOCKED (ADD THIS PART)
        // -----------------------------------------------------
        if (otpRedisService.isPhoneBlocked(normalizedPhone)) {
            throw new IllegalStateException("Too many attempts. Please try again later.");
        }

        // -----------------------------------------------------
        // 2. Load OTP hash
        // -----------------------------------------------------
        String storedOtpHash = otpRedisService.getOtpHash(normalizedPhone);
        if (storedOtpHash == null)
            throw new IllegalArgumentException("OTP expired or not requested");

        // -----------------------------------------------------
        // 3. Verify OTP
        // -----------------------------------------------------
        if (!passwordEncoder.matches(otp, storedOtpHash)) {

            int attempts = otpRedisService.increaseAttempts(normalizedPhone);

            if (attempts > maxVerifyAttempts) {
                otpRedisService.deleteOtp(normalizedPhone);

                // -----------------------------------------------------
                // 🔐 4. BLOCK PHONE IF TOO MANY FAILED ATTEMPTS (ADD THIS LINE)
                // -----------------------------------------------------
                otpRedisService.blockPhone(normalizedPhone, blockSeconds);

                throw new IllegalStateException("Too many invalid OTP attempts");
            }

            throw new IllegalArgumentException("Invalid OTP");
        }

        // -----------------------------------------------------
        // 5. OTP valid → cleanup
        // -----------------------------------------------------
        otpRedisService.deleteOtp(normalizedPhone);

        // -----------------------------------------------------
        // 6. User exists → return token or temp token
        // -----------------------------------------------------
        Optional<User> found = userRepository.findByMobileNumber(normalizedPhone);

        if (found.isPresent()) {
            User user = found.get();

            if (!user.isProfileComplete()) {
                String tempToken = generateJwtToken(user);
                return new VerifyOtpResult(true, null, user.getUserId(), tempToken);
            }

            return new VerifyOtpResult(false, buildLoginResponse(user));
        }

        // -----------------------------------------------------
        // 7. No user → new-user flow
        // -----------------------------------------------------
        String tempToken = generateTempTokenForPhone(normalizedPhone);
        return new VerifyOtpResult(true, null, null, tempToken);
    }


    private UserLoginResponseDTO buildLoginResponse(User user) {
        String jwt = generateJwtToken(user);
        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
        String oauthProvider = user.getOauthAccounts().stream()
                .findFirst()
                .map(acc -> acc.getProvider().name())
                .orElse(OAuthAccount.AuthProvider.MOBILE.name());
        boolean isProfileCompleted = user.isProfileComplete();

        return new UserLoginResponseDTO(
                user.getUserId(),
                user.getEmail(),
                roles,
                jwt,
                oauthProvider,
                isProfileCompleted
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserLoginResponseDTO completeProfile(CompleteProfileDTO dto) {
        String phone = PhoneUtils.normalizeToE164(dto.getPhone());
        if (phone == null) throw new IllegalArgumentException("Invalid phone number format");

        String email = dto.getEmail().toLowerCase(Locale.ROOT);

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()
                || dto.getLastName() == null || dto.getLastName().isBlank()
                || email.isBlank()) {
            throw new IllegalArgumentException("Missing required profile fields");
        }

        Optional<User> existingUserOpt = userRepository.findByMobileNumber(phone);
        User user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent() && !byEmail.get().getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("Email already exists. Please use a different email.");
            }

            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(email);
            user.setProfileComplete(true);
            user.setVerified(true);

            logger.info("Updating existing user profile for phone: {}", phone);
        } else {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists. Please use a different email.");
            }

            user = new User();
            user.setMobileNumber(phone);
            user.setVerified(true);
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(CommonUtils.generateSecureRandomPassword()));
            user.setProfileComplete(true);

            boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
            Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
            roleAssignmentUtil.assignRoles(user, roles);

            logger.info("Creating new user for phone: {}", phone);
        }

        User saved = userRepository.save(user);
        logger.info("Profile completed successfully for phone: {}, email: {}", phone, email);

        return buildLoginResponse(saved);
    }

    public String generateTempTokenForPhone(String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        // You can also add "purpose":"profile_completion" claim
        claims.put("purpose", "profile_completion");
        // short-lived token
        return jwtUtil.generateToken(claims, phone, TEMP_TOKEN_TTL_MS);
    }

    public boolean validateTempTokenForPhone(String token, String phone) {
        try {
            Claims c = jwtUtil.parseToken(token);
            if (!"profile_completion".equals(c.get("purpose"))) return false;
            String tokenPhone = c.get("phone", String.class);
            return phone.equals(tokenPhone) && !jwtUtil.isTokenExpired(token);
        } catch (JwtException ex) {
            return false;
        }
    }

}
