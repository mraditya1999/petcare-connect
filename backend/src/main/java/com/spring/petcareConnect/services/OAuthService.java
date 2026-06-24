package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.auth.response.LoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.request.OAuthLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.request.OtpLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.response.OAuthAuthUrlResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.OAuthProfileResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.OtpLoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.VerifyOtpResponseDto;
import com.spring.petcareConnect.dtos.profile.request.CompleteProfileRequestDto;
import com.spring.petcareConnect.enums.AuthProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface OAuthService {

    LoginResponseDto completeProfile(Authentication authentication, HttpServletRequest httpServletRequest, CompleteProfileRequestDto completeProfileRequestDTO);

    OtpLoginResponseDto sendOtp(OtpLoginRequestDto otpLoginRequestDto);

    VerifyOtpResponseDto verifyOtpAndLogin(String phone, String otp);

    OAuthAuthUrlResponseDto generateGoogleAuthUrl();

    OAuthAuthUrlResponseDto generateGithubAuthUrl();

    LoginResponseDto processOAuthLogin(AuthProvider provider, OAuthProfileResponseDto profile, String rawAccessToken);

    LoginResponseDto googleLogin(OAuthLoginRequestDto request);

    LoginResponseDto githubLogin(OAuthLoginRequestDto request);
}