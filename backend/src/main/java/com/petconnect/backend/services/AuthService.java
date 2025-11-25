package com.petconnect.backend.services;

import com.petconnect.backend.dto.user.GoogleUserDTO;
import com.petconnect.backend.entity.OAuthAccount;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.repositories.OAuthAccountRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.security.JwtUtil;
import com.petconnect.backend.security.UserDetailsServiceImpl;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final JwtUtil jwtUtil;
    private final TempUserStore tempUserStore;
    private final OAuthAccountRepository oauthAccountRepository;

    @Autowired
    public AuthService(UserRepository userRepository,
                       RoleAssignmentUtil roleAssignmentUtil,
                       @Lazy PasswordEncoder passwordEncoder,
                       @Lazy VerificationService verificationService,
                       JwtUtil jwtUtil,
                       TempUserStore tempUserStore,
                       OAuthAccountRepository oauthAccountRepository) {
        this.userRepository = userRepository;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.jwtUtil = jwtUtil;
        this.tempUserStore = tempUserStore;
        this.oauthAccountRepository = oauthAccountRepository;
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
        email = email.toLowerCase(Locale.ROOT);
        String finalEmail = email;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + finalEmail));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                .collect(Collectors.toSet());

        // If password is null (OAuth-only account), supply empty string to avoid NPE in Spring User constructor.
        String pwd = user.getPassword() == null ? "" : user.getPassword();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), pwd, authorities);
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Transactional
    public void registerUser(User user) {
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        // password expected for local registration
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Password is required for local registration");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
        roleAssignmentUtil.assignRoles(user, roles);

        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
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
    public Optional<User> authenticateUser(String email, String password) {
        email = email.toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (user.getPassword() == null) {
            // This account does not have a password (likely OAuth-only)
            logger.warn("Attempt to password-authenticate OAuth-only account: {}", email);
            throw new AuthenticationException("Invalid email or password.");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("User authenticated with email: {}", email);
            return Optional.of(user);
        } else {
            logger.warn("Invalid email or password for email: {}", email);
            throw new AuthenticationException("Invalid email or password.");
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
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserDTO.class);
        return response.getBody();
    }

    /**
     * Scalable, robust Google login handler:
     *  - prefer provider lookup (provider + providerUserId)
     *  - fallback to email matching (linking) if provider record not found
     *  - create user if not exist
     *  - create OAuthAccount only when missing
     */
    @Transactional
    public User processGoogleLogin(GoogleUserDTO profile, String rawAccessToken) {

        if (profile.getSub() == null || profile.getEmail() == null) {
            throw new IllegalArgumentException("Invalid Google profile: missing sub or email");
        }

        String googleId = profile.getSub();
        String email = profile.getEmail().toLowerCase(Locale.ROOT);

        // ---------------------------------------------------------
        // 1) Check if this exact Google account already exists
        // ---------------------------------------------------------
        Optional<OAuthAccount> existingAccountOpt =
                oauthAccountRepository.findByProviderAndProviderUserId(
                        OAuthAccount.AuthProvider.GOOGLE,
                        googleId
                );

        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            User existingUser = existingAccount.getUser();

            boolean changed = false;

            if (rawAccessToken != null && !rawAccessToken.isBlank()) {
                existingAccount.setAccessToken(rawAccessToken);
                existingAccount.setTokenExpiry(Instant.now().plusSeconds(3600));
                changed = true;
            }

            changed |= updateGoogleUserInfoIfNeeded(existingUser, profile);

            if (changed) {
                oauthAccountRepository.save(existingAccount);
                userRepository.save(existingUser);
            }
            return existingUser;
        }

        // ---------------------------------------------------------
        // 2) No Google OAuth record found → maybe email exists
        // ---------------------------------------------------------
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {

            // SECURITY CHECK → Prevent linking different Google IDs
            oauthAccountRepository.findByUser(user).forEach(acc -> {
                if (acc.getProvider() == OAuthAccount.AuthProvider.GOOGLE &&
                        !acc.getProviderUserId().equals(googleId)) {
                    throw new IllegalStateException(
                            "Email already linked with another Google account."
                    );
                }
            });

            boolean changed = updateGoogleUserInfoIfNeeded(user, profile);

            if (!user.isVerified()) {
                user.setVerified(true);
            }

            if (changed) userRepository.save(user);

        } else {
            // ---------------------------------------------------------
            // 3) New Google user → create user
            // ---------------------------------------------------------
            user = new User();
            user.setEmail(email);
            user.setFirstName(profile.getGiven_name() != null ? profile.getGiven_name() : "Google");
            user.setLastName(profile.getFamily_name() != null ? profile.getFamily_name() : "User");
            user.setAvatarUrl(profile.getPicture());
            user.setVerified(true);

            // FIX: Prevent validation failure → password MUST NOT be null or empty
            String randomPassword = generateSecureRandomPassword();
            user.setPassword(passwordEncoder.encode(randomPassword));

            // Assign roles normally
            boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
            Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
            roleAssignmentUtil.assignRoles(user, roles);

            user = userRepository.save(user);
        }

        // ---------------------------------------------------------
        // 4) Create OAuthAccount entry for Google
        // ---------------------------------------------------------
        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.setUser(user);
        oauthAccount.setProvider(OAuthAccount.AuthProvider.GOOGLE);
        oauthAccount.setProviderUserId(googleId);

        if (rawAccessToken != null && !rawAccessToken.isBlank()) {
            oauthAccount.setAccessToken(rawAccessToken);
            oauthAccount.setTokenExpiry(Instant.now().plusSeconds(3600));
        }

        oauthAccountRepository.save(oauthAccount);
        user.getOauthAccounts().add(oauthAccount);
        return user;
    }


    private boolean updateGoogleUserInfoIfNeeded(User user, GoogleUserDTO profile) {
        boolean changed = false;

        if (profile.getGiven_name() != null && !profile.getGiven_name().equals(user.getFirstName())) {
            user.setFirstName(profile.getGiven_name());
            changed = true;
        }

        if (profile.getFamily_name() != null && !profile.getFamily_name().equals(user.getLastName())) {
            user.setLastName(profile.getFamily_name());
            changed = true;
        }

        if (profile.getPicture() != null && !profile.getPicture().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(profile.getPicture());
            changed = true;
        }

        //  FIX: Google login MUST have non-empty password or validation fails
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            String randomPassword = generateSecureRandomPassword();
            user.setPassword(passwordEncoder.encode(randomPassword));
            changed = true;
        }

        return changed;
    }


    private String generateSecureRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


}
