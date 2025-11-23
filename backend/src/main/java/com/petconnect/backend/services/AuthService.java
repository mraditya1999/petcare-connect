package com.petconnect.backend.services;

import com.petconnect.backend.dto.user.GoogleUserDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
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

    @Autowired
    public AuthService(UserRepository userRepository, RoleAssignmentUtil roleAssignmentUtil,  @Lazy PasswordEncoder passwordEncoder,
                       @Lazy VerificationService verificationService, JwtUtil jwtUtil, TempUserStore tempUserStore) {
        this.userRepository = userRepository;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.jwtUtil = jwtUtil;
        this.tempUserStore = tempUserStore;
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
        //  Normalize email
        email = email.toLowerCase(Locale.ROOT);

        String finalEmail = email;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + finalEmail));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Transactional
    public void registerUser(User user) {
        //  Normalize email
        user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        user.setOauthProvider(User.AuthProvider.LOCAL);
        user.setOauthProviderId(null);

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
        //  Normalize email
        email = email.toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

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


    public User processGoogleLogin(GoogleUserDTO profile) {
        if (profile.getSub() == null || profile.getEmail() == null) {
            throw new IllegalArgumentException("Invalid Google profile: missing sub or email");
        }

        //  Normalize Google email
        String normalizedEmail = profile.getEmail().toLowerCase(Locale.ROOT);
        profile.setEmail(normalizedEmail);

        User user = userRepository.findByOauthProviderId(profile.getSub()).orElse(null);

        if (user == null) {
            user = userRepository.findByEmail(normalizedEmail).orElse(null);
            if (user != null && user.getOauthProviderId() == null) {
                user.setOauthProvider(User.AuthProvider.GOOGLE);
                user.setOauthProviderId(profile.getSub());
            }
        }

        if (user == null) {
            user = new User();
            user.setEmail(normalizedEmail);
            user.setFirstName(profile.getGiven_name() != null ? profile.getGiven_name() : "Google");
            user.setLastName(profile.getFamily_name() != null ? profile.getFamily_name() : "User");
            user.setAvatarUrl(profile.getPicture());
            user.setOauthProvider(User.AuthProvider.GOOGLE);
            user.setOauthProviderId(profile.getSub());
            user.setEmailVerified(true);

            String randomPassword = generateSecureRandomPassword();
            user.setPassword(passwordEncoder.encode(randomPassword));

            boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
            Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
            roleAssignmentUtil.assignRoles(user, roles);
        } else {
            user.setFirstName(profile.getGiven_name() != null ? profile.getGiven_name() : user.getFirstName());
            user.setLastName(profile.getFamily_name() != null ? profile.getFamily_name() : user.getLastName());
            user.setAvatarUrl(profile.getPicture() != null ? profile.getPicture() : user.getAvatarUrl());
            user.setEmailVerified(true);
        }

        return userRepository.save(user);
    }

    private String generateSecureRandomPassword() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }


}
