package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.auth.response.LoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.request.OAuthLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.request.OtpLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.response.*;
import com.spring.petcareConnect.dtos.profile.request.CompleteProfileRequestDto;
import com.spring.petcareConnect.entities.OAuthAccount;
import com.spring.petcareConnect.entities.Role;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.AuthProvider;
import com.spring.petcareConnect.enums.RoleName;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.DuplicateResourceException;
import com.spring.petcareConnect.exceptions.OtpException;
import com.spring.petcareConnect.exceptions.TokenException;
import com.spring.petcareConnect.repositories.jpa.OAuthAccountRepository;
import com.spring.petcareConnect.repositories.jpa.RoleRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.services.OAuthService;
import com.spring.petcareConnect.security.jwt.JwtUtils;
import com.spring.petcareConnect.services.OAuthStateService;
import com.spring.petcareConnect.services.OtpRedisService;
import com.spring.petcareConnect.services.SmsService;
import com.spring.petcareConnect.utils.EmailUtils;
import com.spring.petcareConnect.utils.PhoneUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.security.util.SecurityUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Value("${otp.ttl.minutes}")
    private int ttlMinutes;

    @Value("${otp.max.verify.attempts}")
    private int maxVerifyAttempts;

    @Value("${otp.block.seconds}")
    private int blockSeconds;

    @Value("${otp.temp.token.expiry}")
    private int tempTokenExpirySeconds;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.resend.cooldown}")
    private int resendCooldownSeconds;

    @Value("${otp.max.send.per.hour}")
    private int maxSendPerHour;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    private static final Logger logger = LoggerFactory.getLogger(OAuthServiceImpl.class);

    private final OAuthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final OtpRedisService otpRedisService;
    private final OAuthStateService oAuthStateService;
    private final SmsService smsService;
    private final WebClient webClient;


    public OAuthServiceImpl(OAuthAccountRepository oauthAccountRepository,
                            UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtils jwtUtils, OtpRedisService otpRedisService, OAuthStateService oAuthStateService, SmsService smsService, WebClient webClient) {
        this.oauthAccountRepository = oauthAccountRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.otpRedisService = otpRedisService;
        this.oAuthStateService = oAuthStateService;
        this.smsService = smsService;
        this.webClient = webClient;
    }

    @Override
    public LoginResponseDto completeProfile(Authentication authentication, HttpServletRequest httpServletRequest, CompleteProfileRequestDto completeProfileRequestDto) {
        String token = jwtUtils.getJwtFromHeaders(httpServletRequest);
        String normalizedPhone = PhoneUtils.normalizeToIndianFormat(completeProfileRequestDto.getPhone());
        String email = EmailUtils.normalize(completeProfileRequestDto.getEmail());

        if (!jwtUtils.validateTokenPurpose(token, AppConstants.TEMP_TOKEN_PURPOSE_PROFILE_COMPLETION)) {
            logger.warn("Profile completion failed: invalid purpose for phone={}", normalizedPhone);
            throw new TokenException("ProfileCompletion", "invalid purpose");
        }
        if (jwtUtils.isTokenExpired(token)) {
            logger.warn("Profile completion failed: expired temp token for phone={}", normalizedPhone);
            throw new TokenException("ProfileCompletion", "expired");
        }

        Optional<User> existingUserOpt = userRepository.findByMobileNumber(normalizedPhone);
        User user;

        if (existingUserOpt.isPresent()) {
            // Update existing user
            user = existingUserOpt.get();

            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent() && !byEmail.get().getUserId().equals(user.getUserId())) {
                throw new DuplicateResourceException("User", "email", email);
            }

            user.setFirstName(completeProfileRequestDto.getFirstName());
            user.setLastName(completeProfileRequestDto.getLastName());
            user.setEmail(email);
            user.setProfileComplete(true);
            user.setVerified(true);

            logger.info("Updating existing user profile for phone={}", normalizedPhone);

        } else {
            // Create new user
            if (userRepository.existsByEmail(email)) {
                throw new DuplicateResourceException("User", "email", email);
            }

            user = new User();
            user.setMobileNumber(normalizedPhone);
            user.setVerified(true);
            user.setFirstName(completeProfileRequestDto.getFirstName());
            user.setLastName(completeProfileRequestDto.getLastName());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(SecurityUtils.generateSecureToken(com.spring.petcareConnect.config.AppConstants.SECURE_BYTES)));
            user.setProfileComplete(true);

            long verifiedCount = userRepository.countByVerified(true);
            if (verifiedCount == 0) {
                Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new APIException("ROLE_ADMIN not configured"));
                user.getRoles().clear();
                user.getRoles().add(adminRole);
                logger.info("First verified user will be promoted to ADMIN (phone={})", normalizedPhone);
            }

            logger.info("Creating new user for phone={}", normalizedPhone);
        }


        User savedUser = userRepository.save(user);

        // Build OAuthProfile for MOBILE provider
        OAuthProfileResponseDto mobileOAuthProfile = OAuthProfileResponseDto.fromMobile(savedUser.getMobileNumber());
        // Fill in optional fields if available
        OAuthProfileResponseDto enrichedProfile = new OAuthProfileResponseDto(
                mobileOAuthProfile.getProviderUserId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                null
        );

        // Process OAuth login via OAuthService
        LoginResponseDto loginResp = processOAuthLogin(AuthProvider.MOBILE, enrichedProfile, jwtUtils.generateTokenFromUser(savedUser));
        return loginResp;
    }

    @Override
    public OtpLoginResponseDto sendOtp(OtpLoginRequestDto otpLoginRequestDto) {
        String rawPhone = otpLoginRequestDto.getPhone();
        String normalizedPhone = PhoneUtils.normalizeToIndianFormat(rawPhone);

        if (normalizedPhone == null) {
            logger.warn("OTP send rejected: invalid phone format");
            throw new IllegalArgumentException("Invalid phone number format");
        }

        logger.debug("OTP send requested for phone={}", normalizedPhone);

        // check blocked
        if (otpRedisService.isPhoneBlocked(normalizedPhone)) {
            logger.warn("OTP send blocked for phone={}", normalizedPhone);
            throw OtpException.rateLimited("Phone temporarily blocked due to too many attempts");
        }

        // rate-limit: cooldown and hourly limit
        if (!otpRedisService.canSendOtp(normalizedPhone, resendCooldownSeconds, maxSendPerHour)) {
            int rem = otpRedisService.getRemainingCooldownSeconds(normalizedPhone, resendCooldownSeconds);
            if (rem > 0) {
                logger.warn("OTP send throttled by cooldown for phone={}, remainingSeconds={}", normalizedPhone, rem);
                throw OtpException.rateLimited("Please wait " + rem + " seconds before requesting another OTP");
            } else {
                logger.warn("OTP send throttled by hourly limit for phone={}", normalizedPhone);
                throw OtpException.rateLimited("Hourly OTP limit reached. Try again later.");
            }
        }

        // Generate OTP
        String otp = SecurityUtils.generateNumericOtp(otpLength);
        String hashedOtp = passwordEncoder.encode(otp);

        // Store hashed OTP in Redis
        otpRedisService.storeOtpHash(normalizedPhone, hashedOtp, ttlMinutes * 60L);

        String message = String.format("Your verification code is %s. Expires in %d minutes.", otp, ttlMinutes);
        String smsRecipient = PhoneUtils.toIndianE164(normalizedPhone);
        if (smsRecipient == null) {
            otpRedisService.deleteOtp(normalizedPhone);
            throw new IllegalArgumentException("Invalid phone number format");
        }

        try {
            smsService.sendSms(smsRecipient, message);
        } catch (RuntimeException ex) {
            otpRedisService.deleteOtp(normalizedPhone);
            logger.error("OTP delivery failed for phone={}", normalizedPhone, ex);
            throw OtpException.deliveryFailed("Unable to send OTP. Please try again later.", ex);
        }

        // record send for rate-limiting
        try {
            otpRedisService.recordOtpSent(normalizedPhone);
        } catch (Exception ex) {
            logger.warn("Failed to record OTP send for phone={}", normalizedPhone, ex);
        }

        // Log OTP sent (without exposing code)
        logger.info("OTP sent to phone: {}", normalizedPhone);
        OtpLoginResponseDto otpLoginResponseDto = new OtpLoginResponseDto(normalizedPhone);
        return otpLoginResponseDto;
    }

    @Transactional
    public VerifyOtpResponseDto verifyOtpAndLogin(String phone, String otp) {
        String normalizedPhone = PhoneUtils.normalizeToIndianFormat(phone);
        if (normalizedPhone == null) {
            logger.warn("OTP verification rejected: invalid phone format");
            throw new IllegalArgumentException("Invalid phone number format");
        }

        // -----------------------------------------------------
        // 🔐 1. CHECK IF PHONE IS BLOCKED
        // -----------------------------------------------------
        if (otpRedisService.isPhoneBlocked(normalizedPhone)) {
            logger.warn("OTP verification blocked for phone={}", normalizedPhone);
            throw OtpException.rateLimited("Too many attempts. Please try again later.");
        }

        // -----------------------------------------------------
        // 2. Load OTP hash
        // -----------------------------------------------------
        String storedOtpHash = otpRedisService.getOtpHash(normalizedPhone);
        if (storedOtpHash == null) {
            logger.warn("OTP verification failed: no active OTP for phone={}", normalizedPhone);
            throw new IllegalArgumentException("OTP expired or not requested");
        }

        // -----------------------------------------------------
        // 3. Verify OTP
        // -----------------------------------------------------
        if (!passwordEncoder.matches(otp, storedOtpHash)) {
            int attempts = otpRedisService.increaseAttempts(normalizedPhone);

            if (attempts >= maxVerifyAttempts) {
                otpRedisService.deleteOtp(normalizedPhone);

                // 4. BLOCK PHONE IF TOO MANY FAILED ATTEMPTS
                otpRedisService.blockPhone(normalizedPhone, blockSeconds);

                logger.warn("OTP verification failed and phone blocked: phone={}, attempts={}", normalizedPhone, attempts);
                throw OtpException.rateLimited("Too many invalid OTP attempts");
            }

            logger.warn("OTP verification failed: invalid OTP for phone={}, attempts={}", normalizedPhone, attempts);
            throw new IllegalArgumentException("Invalid OTP");
        }

        // -----------------------------------------------------
        // 5. OTP valid → cleanup
        // -----------------------------------------------------
        otpRedisService.deleteOtp(normalizedPhone);
        logger.info("OTP verified successfully for phone={}", normalizedPhone);

        // -----------------------------------------------------
        // 6. User exists → return token or temp token
        // -----------------------------------------------------
        Optional<User> found = userRepository.findByMobileNumber(normalizedPhone);

        if (found.isPresent()) {
            User user = found.get();

            // Existing user → login response via OAuthService
            LoginResponseDto login = processOAuthLogin(AuthProvider.MOBILE, OAuthProfileResponseDto.fromMobile(user.getMobileNumber()), jwtUtils.generateTokenFromUser(user));
            logger.info("OTP login completed for existing user: phone={}, userId={}", normalizedPhone, user.getUserId());
            return VerifyOtpResponseDto.forExistingUser(login);
        }

        // -----------------------------------------------------
        // 7. No user → new-user flow
        // -----------------------------------------------------
        String tempToken = jwtUtils.generateTempTokenForPhone(normalizedPhone, AppConstants.TEMP_TOKEN_PURPOSE_PROFILE_COMPLETION, tempTokenExpirySeconds);
        logger.info("OTP verified for new user profile completion: phone={}", normalizedPhone);
        return VerifyOtpResponseDto.forNewUser(normalizedPhone, tempToken);
    }

    @Transactional
    public LoginResponseDto googleLogin(OAuthLoginRequestDto request) {
        validateOAuthState(request.getState());
        String accessToken = exchangeGoogleCodeForAccessToken(request.getCode());
        OAuthProfileResponseDto profile = fetchProfile(AuthProvider.GOOGLE, accessToken);

        User user = resolveUser(AuthProvider.GOOGLE, profile, accessToken);
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
        }

        return buildLoginResponse(user, AuthProvider.GOOGLE.name());
    }

    @Transactional
    public LoginResponseDto githubLogin(OAuthLoginRequestDto request) {
        validateOAuthState(request.getState());
        String accessToken = exchangeGithubCodeForAccessToken(request.getCode());
        OAuthProfileResponseDto profile = fetchProfile(AuthProvider.GITHUB, accessToken);

        User user = resolveUser(AuthProvider.GITHUB, profile, accessToken);
        return buildLoginResponse(user, AuthProvider.GITHUB.name());
    }

    private User resolveUser(AuthProvider provider, OAuthProfileResponseDto profile, String accessToken) {
        if ((profile.getProviderUserId() == null || profile.getProviderUserId().isBlank()) &&
                (profile.getEmail() == null || profile.getEmail().isBlank())) {
            throw new APIException("Invalid profile: missing providerUserId or email");
        }

        final String providerUserId = profile.getProviderUserId();
        final String email = (profile.getEmail() != null) ? profile.getEmail().toLowerCase(Locale.ROOT) : null;

        logger.debug("Resolving user for provider={}, providerUserId={}, email={}", provider, providerUserId, email);

        // 1) Check existing OAuth account
        Optional<OAuthAccount> existingAccountOpt = oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);
        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            User existingUser = existingAccount.getUser();

            if (accessToken != null && !accessToken.isBlank()) {
                existingAccount.setAccessToken(accessToken);
                existingAccount.setTokenExpiry(Instant.now().plusSeconds(AppConstants.OAUTH_TOKEN_EXPIRY_SECONDS));
                oauthAccountRepository.save(existingAccount);
            }

            boolean changed = updateUserFromProfileIfNeeded(existingUser, profile);
            if (changed) {
                userRepository.save(existingUser);
            }
            return existingUser;
        }

        // 2) Fallback by email
        User user = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        if (user != null) {
            // Ensure no conflicting provider linkage
            oauthAccountRepository.findByUser(user).forEach(acc -> {
                if (acc.getProvider() == provider && !acc.getProviderUserId().equals(providerUserId)) {
                    String msg = "Email already linked with another " + provider + " account.";
                    logger.warn("{} userEmail={}, existingProviderUserId={}, newProviderUserId={}", msg, email, acc.getProviderUserId(), providerUserId);
                    throw new APIException(msg);
                }
            });

            boolean changed = updateUserFromProfileIfNeeded(user, profile);
            if (!user.isVerified()) {
                user.setVerified(true);
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
            }
        } else {
            // 3) Create new user
            user = createUserFromProfile(profile);
            logger.info("Created new user {} for provider {}", user.getEmail(), provider);
        }

        // 4) Link OAuth account
        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.setUser(user);
        oauthAccount.setProvider(provider);
        oauthAccount.setProviderUserId(providerUserId);

        if (accessToken != null && !accessToken.isBlank()) {
            oauthAccount.setAccessToken(accessToken);
            oauthAccount.setTokenExpiry(Instant.now().plusSeconds(AppConstants.OAUTH_TOKEN_EXPIRY_SECONDS));
        }

        oauthAccountRepository.save(oauthAccount);

        if (!user.getOauthAccounts().contains(oauthAccount)) {
            user.getOauthAccounts().add(oauthAccount);
            userRepository.save(user);
        }

        return user;
    }

    private void validateOAuthState(String state) {
        if (state == null || state.isBlank() || oAuthStateService.getOAuthState(state) == null) {
            throw new IllegalStateException("Invalid OAuth state");
        }
        oAuthStateService.deleteOAuthState(state);
    }

    private String exchangeGoogleCodeForAccessToken(String code) {
        return exchangeCodeForAccessToken(AppConstants.GOOGLE, googleClientId, googleClientSecret, googleRedirectUri, code, true);
    }

    private String exchangeGithubCodeForAccessToken(String code) {
        return exchangeCodeForAccessToken(AppConstants.GITHUB, githubClientId, githubClientSecret, githubRedirectUri, code, false);
    }

    private String exchangeCodeForAccessToken(String providerName, String clientId, String clientSecret, String redirectUri, String code, boolean isGoogle) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank() || redirectUri == null || redirectUri.isBlank()) {
            throw new APIException(providerName + " OAuth credentials not configured");
        }
        if (code == null || code.isBlank()) {
            throw new APIException(providerName + " authorization code is required");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        if (isGoogle) {
            body.add("grant_type", "authorization_code");
        }

        String url = isGoogle ? AppConstants.GOOGLE_TOKEN_URL : AppConstants.GITHUB_TOKEN_URL;
        return exchangeAuthorizationCode(url, body, providerName);
    }


    private String exchangeAuthorizationCode(String url, org.springframework.util.MultiValueMap<String, String> body, String providerName) {
        try {
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(body))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .doOnError(error -> logger.error("{} OAuth error: {}", providerName, error.getMessage()))
                    .block();

            if (response == null) {
                logger.error("Empty body returned from {} token exchange", providerName);
                throw new IllegalStateException("Failed to exchange code for access token");
            }

            Object token = response.get("access_token");
            if (token == null) {
                logger.error("No access_token in response from {}. Full response: {}", providerName, response);
                throw new IllegalStateException("No access token returned from " + providerName);
            }

            logger.info("Successfully exchanged {} code for access token", providerName);
            return token.toString();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            logger.error("{} OAuth error: {} - {}", providerName, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new IllegalStateException("Failed to exchange code with " + providerName + ": " + ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error during {} token exchange: {}", providerName, ex.getMessage(), ex);
            throw new IllegalStateException("Failed to exchange code with " + providerName + ": " + ex.getMessage(), ex);
        }
    }

    @Override
    public OAuthAuthUrlResponseDto generateGoogleAuthUrl() {
        return generateAuthUrl(AppConstants.GOOGLE, googleClientId, googleRedirectUri,
                AppConstants.GOOGLE_SCOPE, AppConstants.GOOGLE_AUTH_URL_TEMPLATE);
    }

    @Override
    public OAuthAuthUrlResponseDto generateGithubAuthUrl() {
        return generateAuthUrl(AppConstants.GITHUB, githubClientId, githubRedirectUri,
                AppConstants.GITHUB_SCOPE, AppConstants.GITHUB_AUTH_URL_TEMPLATE);
    }

    private OAuthAuthUrlResponseDto generateAuthUrl(String providerName,
                                                    String clientId,
                                                    String redirectUri,
                                                    String scope,
                                                    String baseUrl) {
        String state = UUID.randomUUID().toString();
        oAuthStateService.saveOAuthState(state, Duration.ofMinutes(10));

        String url = String.format(baseUrl,
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8),
                URLEncoder.encode(state, StandardCharsets.UTF_8));

        return new OAuthAuthUrlResponseDto(providerName, url, redirectUri, state);
    }

    private OAuthProfileResponseDto fetchProfile(AuthProvider provider, String accessToken) {
        if (provider == AuthProvider.GOOGLE) {
            GoogleUserResponseDto googleUser = webClient.get()
                    .uri(AppConstants.GOOGLE_PROFILE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(GoogleUserResponseDto.class)
                    .block();
            return OAuthProfileResponseDto.fromGoogle(googleUser);
        } else if (provider == AuthProvider.GITHUB) {
            GitHubUserResponseDto gitHubUser = webClient.get()
                    .uri(AppConstants.GITHUB_PROFILE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(GitHubUserResponseDto.class)
                    .block();
            return OAuthProfileResponseDto.fromGitHub(gitHubUser);
        } else {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    @Override
    public LoginResponseDto processOAuthLogin(AuthProvider provider, OAuthProfileResponseDto profile, String rawAccessToken) {
        if ((profile.getProviderUserId() == null || profile.getProviderUserId().isBlank()) && (profile.getEmail() == null || profile.getEmail().isBlank())) {
            logger.warn("OAuth login rejected: provider={} missing providerUserId and email", provider);
            throw new IllegalArgumentException("Invalid profile: missing providerUserId or email");
        }

        final String providerUserId = profile.getProviderUserId();
        final String email = (profile.getEmail() != null) ? profile.getEmail().toLowerCase(Locale.ROOT) : null;

        logger.debug("Processing OAuth login: provider={}, providerUserIdPresent={}, emailPresent={}",
                provider, providerUserId != null && !providerUserId.isBlank(), email != null && !email.isBlank());

        Optional<OAuthAccount> existingAccountOpt = oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);
        User user;

        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            if (rawAccessToken != null && !rawAccessToken.isBlank()) {
                existingAccount.setAccessToken(rawAccessToken);
                oauthAccountRepository.save(existingAccount);
            }
            user = existingAccount.getUser();
            logger.info("OAuth login matched existing account: provider={}, userId={}", provider, user.getUserId());
        } else {
            // try by email
            user = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
            if (user == null) {
                // try by mobile (profile id may be mobile)
                user = userRepository.findByMobileNumber(providerUserId).orElse(null);
            }

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setMobileNumber(profile.getProviderUserId());
                user.setFirstName(profile.getFirstName());
                user.setLastName(profile.getLastName());
                user.setPassword(passwordEncoder.encode(SecurityUtils.generateRandomPassword(8)));
                user.setVerified(true);
                user.setProfileComplete(false);
                Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new APIException("ROLE_USER not configured"));
                user.getRoles().add(userRole);
                user = userRepository.save(user);
                logger.info("Created new OAuth user: provider={}, userId={}", provider, user.getUserId());
            } else {
                logger.info("Linking OAuth account to existing user: provider={}, userId={}", provider, user.getUserId());
            }

            OAuthAccount oauthAccount = new OAuthAccount();
            oauthAccount.setUser(user);
            oauthAccount.setProvider(provider);
            oauthAccount.setProviderUserId(providerUserId);
            if (rawAccessToken != null && !rawAccessToken.isBlank()) {
                oauthAccount.setAccessToken(rawAccessToken);
            }
            oauthAccountRepository.save(oauthAccount);

            if (!user.getOauthAccounts().contains(oauthAccount)) {
                user.getOauthAccounts().add(oauthAccount);
                userRepository.save(user);
            }
            logger.info("OAuth account linked: provider={}, userId={}", provider, user.getUserId());
        }

        // Build login response
        List<String> roles = user.getRoles().stream().map(r -> r.getRoleName().name()).collect(Collectors.toList());

        String jwt = jwtUtils.generateTokenFromUser(user);

        return new LoginResponseDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles,
                jwt,
                provider.name(),
                user.isProfileComplete(),
                user.isVerified()
        );
    }

    private User createUserFromProfile(OAuthProfileResponseDto profile) {
        User user = new User();
        user.setEmail(profile.getEmail() != null ? profile.getEmail().toLowerCase(Locale.ROOT) : null);
        user.setFirstName(profile.getFirstName() != null ? profile.getFirstName() : "User");
        user.setLastName(profile.getLastName() != null ? profile.getLastName() : "User");
        user.setAvatarUrl(profile.getAvatarUrl());
        user.setVerified(true);

        String randomPassword = passwordEncoder.encode(SecurityUtils.generateRandomPassword(8));
        user.setPassword(randomPassword);

        long verifiedCount = userRepository.countByVerified(true);
        if (verifiedCount == 0) {
            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new APIException("ROLE_ADMIN not configured"));
            user.getRoles().clear();
            user.getRoles().add(adminRole);
        } else {
            Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new APIException("ROLE_USER not configured"));
            user.getRoles().clear();
            user.getRoles().add(userRole);
        }

        return userRepository.save(user);
    }

    private boolean updateUserFromProfileIfNeeded(User user, OAuthProfileResponseDto profile) {
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
            String randomPassword = passwordEncoder.encode(SecurityUtils.generateRandomPassword(8));
            user.setPassword(randomPassword);
            changed = true;
        }
        return changed;
    }

    private LoginResponseDto buildLoginResponse(User user, String providerName) {
        String jwtToken = jwtUtils.generateTokenFromUser(user);
        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponseDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles,
                jwtToken,
                providerName,
                user.isProfileComplete(),
                user.isVerified()
        );
    }
}

