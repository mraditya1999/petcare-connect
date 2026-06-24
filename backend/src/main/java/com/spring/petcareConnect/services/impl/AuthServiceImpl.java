package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.dtos.auth.request.*;
import com.spring.petcareConnect.dtos.auth.response.*;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistCreationDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;
import com.spring.petcareConnect.entities.*;
import com.spring.petcareConnect.enums.AuthProvider;
import com.spring.petcareConnect.enums.EmailType;
import com.spring.petcareConnect.enums.RoleName;
import com.spring.petcareConnect.exceptions.*;
import com.spring.petcareConnect.repositories.jpa.RoleRepository;
import com.spring.petcareConnect.repositories.jpa.SpecialistRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.security.jwt.JwtUtils;
import com.spring.petcareConnect.security.service.UserDetailsImpl;
import com.spring.petcareConnect.services.AuthService;
import com.spring.petcareConnect.services.EmailService;
import com.spring.petcareConnect.utils.AuthUtils;
import com.spring.petcareConnect.utils.EmailUtils;
import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SpecialistRepository specialistRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           SpecialistRepository specialistRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.specialistRepository = specialistRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegistrationResponseDto registerUser(RegistrationRequestDto registrationRequestDto) {
        String email = EmailUtils.normalize(registrationRequestDto.getEmail());
        registrationRequestDto.setEmail(email);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("User", "email", email);
        }

        User user = modelMapper.map(registrationRequestDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        user.setProfileComplete(false);

        String token = jwtUtils.generateEmailVerificationToken(email, 7200);
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(2));

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new APIException("ROLE_USER not configured"));
        user.getRoles().add(userRole);

        user = userRepository.save(user);
        emailService.sendEmail(user, EmailType.VERIFICATION);

        return new RegistrationResponseDto(user.getUserId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }

    @Override
    public VerifyEmailResponseDto verifyUser(VerifyEmailRequestDto verifyEmailRequestDto) {
        String verificationToken = verifyEmailRequestDto.getVerificationToken();

        if (!jwtUtils.validateTokenPurpose(verificationToken, AppConstants.TOKEN_PURPOSE_EMAIL_VERIFICATION)) {
            throw new TokenException("Verification", "invalid purpose");
        }
        if (jwtUtils.isTokenExpired(verificationToken)) {
            throw new TokenException("Verification", "expired");
        }

        String email = jwtUtils.parseToken(verificationToken).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));

        if (user.isVerified()) {
            throw new TokenException("Verification", "already verified");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        boolean promotedToAdmin = false;
        if (userRepository.countByVerified(true) == 0) {
            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new APIException("ROLE_ADMIN not configured"));
            user.getRoles().clear();
            user.getRoles().add(adminRole);
            promotedToAdmin = true;
        }

        userRepository.save(user);
        List<RoleName> roles = user.getRoles().stream().map(Role::getRoleName).toList();
        return new VerifyEmailResponseDto(true, roles, promotedToAdmin);
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        String email = EmailUtils.normalize(loginRequestDto.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isVerified()) {
            throw new BadCredentialsException("Email not verified. Please verify your email first.");
        }
        if (user.isAccountLocked()) {
            throw new BadCredentialsException("User account is locked");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = AuthUtils.loggedInUser().orElseThrow(() -> new BadCredentialsException("Invalid email or password"));


        String accessToken = jwtUtils.generateLoginToken(userDetails);

        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime refreshExpiry = LocalDateTime.now().plusDays(7);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshExpiry);
        userRepository.save(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles().stream().map(role -> role.getRoleName().name()).toList());
        response.setAccessToken(accessToken);
        response.setTokenType("Bearer");
        response.setRefreshToken(refreshToken);
        response.setOauthProvider(AuthProvider.LOCAL.name());
        response.setProfileComplete(user.isProfileComplete());
        response.setVerified(user.isVerified());

        Date tokenExpiry = jwtUtils.getExpirationDateFromJwtToken(accessToken);
        response.setExpiresAt(tokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        response.setExpiresIn((tokenExpiry.getTime() - System.currentTimeMillis()) / 1000);

        return response;
    }

    @Override
    public LoginResponseDto refreshAccessToken(RefreshAccessTokenRequestDto refreshAccessTokenRequestDto) {
        User user = userRepository.findByRefreshToken(refreshAccessTokenRequestDto.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenException("Refresh", "expired");
        }

        String newRefreshToken = UUID.randomUUID().toString();
        LocalDateTime refreshExpiry = LocalDateTime.now().plusDays(7);
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(refreshExpiry);
        userRepository.save(user);

        String newAccessToken = jwtUtils.generateLoginToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream().map(role -> role.getRoleName().name()).toArray(String[]::new))
                        .build()
        );

        if (!jwtUtils.validateTokenPurpose(newAccessToken, AppConstants.TOKEN_PURPOSE_LOGIN)) {
            throw new TokenException("Refresh", "invalid purpose");
        }

        LoginResponseDto response = new LoginResponseDto();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setAccessToken(newAccessToken);
        response.setTokenType("Bearer");
        response.setRefreshToken(newRefreshToken);

        Date tokenExpiry = jwtUtils.getExpirationDateFromJwtToken(newAccessToken);
        response.setExpiresAt(tokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        response.setExpiresIn((tokenExpiry.getTime() - System.currentTimeMillis()) / 1000);

        return response;
    }

    @Override
    public LogoutResponseDto logoutUser(LogoutRequestDto logoutRequestDto) {
        LogoutResponseDto response = new LogoutResponseDto();
        response.setSessionEnded(false);
        response.setRevokedTokenType("refresh");

        // Optional: log who initiated logout (if authenticated)
        AuthUtils.loggedInUserId().ifPresent(id ->
                logger.info("Logout requested by logged-in userId={}", id)
        );

        userRepository.findByRefreshToken(logoutRequestDto.getRefreshToken()).ifPresent(user -> {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);

            response.setUserId(user.getUserId());
            response.setSessionEnded(true);
            logger.info("User logged out successfully userId={}", user.getUserId());
        });

        return response;
    }

    @Override
    public ForgetPasswordResponseDto forgotPassword(ForgetPasswordRequestDto forgetPasswordRequestDto) {
        String email = EmailUtils.normalize(forgetPasswordRequestDto.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        String resetToken = jwtUtils.generateResetPasswordToken(email, 1800); // 30 minutes expiry
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        emailService.sendEmail(user, EmailType.RESET);

        ForgetPasswordResponseDto response = new ForgetPasswordResponseDto();
        response.setEmail(user.getEmail());
        response.setResetLinkSent(true);
        response.setResetInitiatedAt(LocalDateTime.now());
        response.setResetTokenExpiry(user.getResetTokenExpiry());

        return response;
    }

    @Override
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        String token = resetPasswordRequestDto.getToken();
        String newPassword = resetPasswordRequestDto.getNewPassword();

        if (!jwtUtils.validateTokenPurpose(token, AppConstants.TOKEN_PURPOSE_RESET_PASSWORD)) {
            throw new TokenException("Reset", "invalid purpose");
        }
        if (jwtUtils.isTokenExpired(token)) {
            throw new TokenException("Reset", "expired");
        }

        Claims claims = jwtUtils.parseToken(token);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Reset token has expired");
        }

        if (user.getPassword() != null && passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ValidationException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        ResetPasswordResponseDto response = new ResetPasswordResponseDto();
        response.setEmail(user.getEmail());
        response.setPasswordReset(true);

        return response;
    }

    @Override
    public SpecialistResponseDto createSpecialist(Long adminUserId, SpecialistCreationDto specialistCreationDto) {
        // Alternative: use AuthUtils.loggedInUserId() instead of passing adminUserId
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> ResourceNotFoundException.byId("User", adminUserId));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> r.getRoleName() == RoleName.ROLE_ADMIN);
        if (!isAdmin) {
            throw new APIException("Only ADMIN users can create specialists");
        }

        String email = EmailUtils.normalize(specialistCreationDto.getEmail());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("User", "email", email);
        }

        User specialistUser = new User();
        specialistUser.setFirstName(specialistCreationDto.getFirstName());
        specialistUser.setLastName(specialistCreationDto.getLastName());
        specialistUser.setEmail(email);
        specialistUser.setPassword(passwordEncoder.encode(specialistCreationDto.getPassword()));
        specialistUser.setVerified(true);
        specialistUser.setProfileComplete(false);

        Role specialistRole = roleRepository.findByRoleName(RoleName.ROLE_SPECIALIST)
                .orElseThrow(() -> new APIException("ROLE_SPECIALIST not configured"));
        specialistUser.getRoles().add(specialistRole);

        specialistUser = userRepository.save(specialistUser);

        Specialist specialist = new Specialist();
        specialist.setUser(specialistUser);
        specialist.setAbout(specialistCreationDto.getAbout());
        specialist.setAvailable(true);
        specialist.setVerified(false);
        specialist = specialistRepository.save(specialist);

        SpecialistResponseDto resp = new SpecialistResponseDto();
        resp.setSpecialistId(specialist.getSpecialistId());
        resp.setUserId(specialistUser.getUserId());
        resp.setFirstName(specialistUser.getFirstName());
        resp.setLastName(specialistUser.getLastName());
        resp.setEmail(specialistUser.getEmail());
        resp.setAbout(specialist.getAbout());
        resp.setVerified(specialist.getUser().isVerified());
        resp.setAvailable(specialist.isAvailable());

        return resp;
    }
}
