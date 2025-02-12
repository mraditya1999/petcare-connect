package com.petconnect.backend.services;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.AuthenticationException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.security.JwtUtil;
import com.petconnect.backend.security.UserPrincipal;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempUserStore tempUserStore;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, @Lazy  PasswordEncoder passwordEncoder,
                       TempUserStore tempUserStore, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tempUserStore = tempUserStore;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Transactional
    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User already exists with email: {}", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        // Assign roles
        assignRolesToUser(user);

        // Save the user temporarily
        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
        emailService.sendVerificationEmail(user);
        logger.info("User registered with email: {}", user.getEmail());
    }

    private void assignRolesToUser(User user) {
        boolean isFirstUser = userRepository.count() == 0;
        Set<Role> roles = new HashSet<>();

        if (isFirstUser) {
            roles.add(fetchRole(Role.RoleName.ADMIN));
            roles.add(fetchRole(Role.RoleName.USER));
        } else {
            roles.add(fetchRole(Role.RoleName.USER));
        }
        user.setRoles(roles);
    }

    private Role fetchRole(Role.RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("{} role not found", roleName);
                    return new RuntimeException(roleName + " role not found");
                });
    }

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

    public String generateJwtToken(User user) {
        UserDetails userDetails = new UserPrincipal(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::getAuthority).toArray());
        String token = jwtUtil.generateToken(claims, userDetails.getUsername());
        logger.info("JWT token generated for user with email: {}", user.getEmail());
        return token;
    }

    @Transactional
    public boolean verifyUser(String verificationToken) {
        User tempUser = tempUserStore.getTemporaryUser(verificationToken);
        if (tempUser != null) {
            tempUser.setVerified(true);
            tempUser.setVerificationToken(null); // Clear the token
            userRepository.save(tempUser);
            logger.info("User verified with token: {}", verificationToken);
            return true;
        }
        logger.warn("Verification token invalid or expired: {}", verificationToken);
        return false;
    }

    @Transactional
    public boolean resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new AuthenticationException("Invalid reset token."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
        logger.info("Password reset for user with email: {}", user.getEmail());
        return true;
    }
}
