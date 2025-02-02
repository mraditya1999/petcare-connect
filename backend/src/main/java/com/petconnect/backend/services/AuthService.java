//package com.petconnect.backend.services;
//
//import com.petconnect.backend.utils.TempUserStore;
//import com.petconnect.backend.entity.Role;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.exceptions.AuthenticationException;
//import com.petconnect.backend.exceptions.UserAlreadyExistsException;
//import com.petconnect.backend.repositories.RoleRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import com.petconnect.backend.security.UserPrincipal;
//import com.petconnect.backend.security.JwtUtil;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class AuthService implements UserDetailsService {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final TempUserStore tempUserStore;
//    private final EmailService emailService;
//    private final JwtUtil jwtUtil;
//
//    @Autowired
//    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, TempUserStore tempUserStore, EmailService emailService, JwtUtil jwtUtil) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.tempUserStore = tempUserStore;
//        this.emailService = emailService;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Transactional
//    public void registerUser(User user) {
//        if (userRepository.existsByEmail(user.getEmail())) {
//            logger.warn("User already exists with email: {}", user.getEmail());
//            throw new UserAlreadyExistsException("User already exists with this email.");
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setVerificationToken(UUID.randomUUID().toString());
//        user.setVerified(false);
//
//        // Check if the user is the first user
//        boolean isFirstUser = userRepository.count() == 0;
//
//        // Assign roles
//        Set<Role> roles = new HashSet<>();
//        if (isFirstUser) {
//            Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN)
//                    .orElseThrow(() -> {
//                        logger.error("Admin role not found");
//                        return new RuntimeException("Admin role not found");
//                    });
//            Role userRole = roleRepository.findByRoleName(Role.RoleName.USER)
//                    .orElseThrow(() -> {
//                        logger.error("User role not found");
//                        return new RuntimeException("User role not found");
//                    });
//            roles.add(adminRole);
//            roles.add(userRole);
//        } else {
//            Role defaultRole = roleRepository.findByRoleName(Role.RoleName.USER)
//                    .orElseThrow(() -> {
//                        logger.error("User role not found");
//                        return new RuntimeException("User role not found");
//                    });
//            roles.add(defaultRole);
//        }
//        user.setRoles(roles);
//
//        // Save the user temporarily
//        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
//        emailService.sendVerificationEmail(user);
//        logger.info("User registered with email: {}", user.getEmail());
//    }
//
//    public Optional<User> authenticateUser(String email, String password) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
//            logger.info("User authenticated with email: {}", email);
//            return userOptional;
//        }
//        logger.warn("Invalid email or password for email: {}", email);
//        throw new AuthenticationException("Invalid email or password.");
//    }
//
//    public String generateJwtToken(User user) {
//        UserDetails userDetails = new UserPrincipal(user);
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", user.getUserId());
//        claims.put("email", user.getEmail());
//        claims.put("roles", user.getRoles().stream().map(Role::getAuthority).toArray());
//
//        String token = jwtUtil.generateToken(claims, userDetails.getUsername());
//        logger.info("JWT token generated for user with email: {}", user.getEmail());
//        return token;
//    }
//
//    @Transactional
//    public boolean verifyUser(String verificationToken) {
//        User tempUser = tempUserStore.getTemporaryUser(verificationToken);
//        if (tempUser != null) {
//            tempUser.setVerified(true);
//            tempUser.setVerificationToken(null); // Clear the token
//            userRepository.save(tempUser);
//            logger.info("User verified with token: {}", verificationToken);
//            return true;
//        }
//        logger.warn("Verification token invalid or expired: {}", verificationToken);
//        return false;
//    }
//
//    @Transactional
//    public boolean resetPassword(String resetToken, String newPassword) {
//        Optional<User> userOptional = userRepository.findByResetToken(resetToken);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            user.setPassword(passwordEncoder.encode(newPassword));
//            userRepository.save(user);
//            logger.info("Password reset for user with email: {}", user.getEmail());
//            return true;
//        }
//        logger.warn("Reset token invalid or expired: {}", resetToken);
//        return false;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findByEmail(username);
//        if (userOptional.isEmpty()) {
//            logger.warn("User not found with username: {}", username);
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//        logger.info("User found with username: {}", username);
//        return new UserPrincipal(userOptional.get());
//    }
//
//    public boolean existsByEmail(String email) {
//        return userRepository.existsByEmail(email);
//    }
//
//}
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
import org.springframework.mail.javamail.JavaMailSender;
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
    private PasswordEncoder passwordEncoder;
    private final TempUserStore tempUserStore;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, TempUserStore tempUserStore, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tempUserStore = tempUserStore;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    @Lazy
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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

        // Check if the user is the first user
        boolean isFirstUser = userRepository.count() == 0;

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (isFirstUser) {
            Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> {
                        logger.error("Admin role not found");
                        return new RuntimeException("Admin role not found");
                    });
            Role userRole = roleRepository.findByRoleName(Role.RoleName.USER)
                    .orElseThrow(() -> {
                        logger.error("User role not found");
                        return new RuntimeException("User role not found");
                    });
            roles.add(adminRole);
            roles.add(userRole);
        } else {
            Role defaultRole = roleRepository.findByRoleName(Role.RoleName.USER)
                    .orElseThrow(() -> {
                        logger.error("User role not found");
                        return new RuntimeException("User role not found");
                    });
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        // Save the user temporarily
        tempUserStore.saveTemporaryUser(user.getVerificationToken(), user);
        emailService.sendVerificationEmail(user);
        logger.info("User registered with email: {}", user.getEmail());
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            logger.info("User authenticated with email: {}", email);
            return userOptional;
        }
        logger.warn("Invalid email or password for email: {}", email);
        throw new AuthenticationException("Invalid email or password.");
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
        Optional<User> userOptional = userRepository.findByResetToken(resetToken);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password reset for user with email: {}", user.getEmail());
            return true;
        }
        logger.warn("Reset token invalid or expired: {}", resetToken);
        return false;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
