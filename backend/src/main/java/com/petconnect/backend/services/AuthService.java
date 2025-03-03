package com.petconnect.backend.services;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

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
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        // Assign roles to User
        boolean isFirstVerifiedUser = userRepository.countByIsVerified(true) == 0;
        Set<Role.RoleName> roles = roleAssignmentUtil.determineRolesForUser(isFirstVerifiedUser);
        roleAssignmentUtil.assignRoles(user, roles);

        // Save the user temporarily
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
}
