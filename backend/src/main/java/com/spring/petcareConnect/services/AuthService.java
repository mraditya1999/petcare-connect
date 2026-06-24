package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.auth.request.*;
import com.spring.petcareConnect.dtos.auth.response.*;
import com.spring.petcareConnect.dtos.oauth.request.OtpLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.response.OtpLoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.VerifyOtpResponseDto;
import com.spring.petcareConnect.dtos.profile.request.CompleteProfileRequestDto;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistCreationDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    RegistrationResponseDto registerUser(RegistrationRequestDto registrationRequestDto);

    VerifyEmailResponseDto verifyUser(VerifyEmailRequestDto verifyEmailRequestDto);

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

    LogoutResponseDto logoutUser(LogoutRequestDto logoutRequestDto);

    LoginResponseDto refreshAccessToken(RefreshAccessTokenRequestDto refreshAccessTokenRequestDto);

    ForgetPasswordResponseDto forgotPassword(ForgetPasswordRequestDto forgetPasswordRequestDto);

    ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    SpecialistResponseDto createSpecialist(Long adminUserId, SpecialistCreationDto dto);

}
