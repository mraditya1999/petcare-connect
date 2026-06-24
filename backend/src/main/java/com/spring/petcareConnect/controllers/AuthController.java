package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.auth.request.*;
import com.spring.petcareConnect.dtos.auth.response.*;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistCreationDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;
import com.spring.petcareConnect.services.AuthService;
import com.spring.petcareConnect.config.ResponseMessages;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomApiResponse<RegistrationResponseDto>> registerUser(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        RegistrationResponseDto responseDto = authService.registerUser(registrationRequestDto);
        CustomApiResponse<RegistrationResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.REGISTRATION_SENT_EMAIL, responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<CustomApiResponse<VerifyEmailResponseDto>> verifyEmail(@Valid @RequestBody VerifyEmailRequestDto verifyEmailRequestDto) {
        VerifyEmailResponseDto responseDto = authService.verifyUser(verifyEmailRequestDto);
        String message = responseDto.isAdmin() ? ResponseMessages.EMAIL_VERIFIED_ADMIN : ResponseMessages.EMAIL_VERIFIED_SUCCESS;
        CustomApiResponse<VerifyEmailResponseDto> response = new CustomApiResponse<>(true, message, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto responseDto = authService.loginUser(loginRequestDto);
        CustomApiResponse<LoginResponseDto> response = new CustomApiResponse<>(true,
                ResponseMessages.LOGIN_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<CustomApiResponse<LoginResponseDto>> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenRequestDto refreshAccessTokenRequestDto) {
        LoginResponseDto responseDto = authService.refreshAccessToken(refreshAccessTokenRequestDto);
        CustomApiResponse<LoginResponseDto> response = new CustomApiResponse<>(true,
                ResponseMessages.REFRESH_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<CustomApiResponse<LogoutResponseDto>> logout(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        LogoutResponseDto responseDto = authService.logoutUser(logoutRequestDto);
        CustomApiResponse<LogoutResponseDto> response = new CustomApiResponse<>(true,
                ResponseMessages.LOGOUT_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<CustomApiResponse<ForgetPasswordResponseDto>> forgotPassword(@Valid @RequestBody ForgetPasswordRequestDto forgetPasswordRequestDto) {
        ForgetPasswordResponseDto responseDto = authService.forgotPassword(forgetPasswordRequestDto);
        CustomApiResponse<ForgetPasswordResponseDto> response = new CustomApiResponse<>(true,
                ResponseMessages.PASSWORD_RESET_SENT, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomApiResponse<ResetPasswordResponseDto>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        ResetPasswordResponseDto resetPasswordResponseDto = authService.resetPassword(resetPasswordRequestDto);
        CustomApiResponse<ResetPasswordResponseDto> responseDto = new CustomApiResponse<>(true,
                ResponseMessages.PASSWORD_RESET_SUCCESS, resetPasswordResponseDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/specialists")
    public ResponseEntity<CustomApiResponse<SpecialistResponseDto>> createSpecialist(@RequestParam Long adminId, @Valid @RequestBody SpecialistCreationDto dto) {
        SpecialistResponseDto responseDto = authService.createSpecialist(adminId, dto);
        CustomApiResponse<SpecialistResponseDto> response = new CustomApiResponse<>(true,
                "Specialist created successfully.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}