package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.auth.response.LoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.request.OAuthLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.request.OtpLoginRequestDto;
import com.spring.petcareConnect.dtos.oauth.request.VerifyOtpRequestDto;
import com.spring.petcareConnect.dtos.oauth.response.OAuthAuthUrlResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.OtpLoginResponseDto;
import com.spring.petcareConnect.dtos.oauth.response.VerifyOtpResponseDto;
import com.spring.petcareConnect.dtos.profile.request.CompleteProfileRequestDto;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.services.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

	private final OAuthService oauthService;

	public OAuthController(OAuthService oauthService) {
		this.oauthService = oauthService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<CustomApiResponse<OtpLoginResponseDto>> sendOtp(@Valid @RequestBody OtpLoginRequestDto otpLoginRequestDto) {
        OtpLoginResponseDto otpLoginResponseDto =  oauthService.sendOtp(otpLoginRequestDto);
        CustomApiResponse<OtpLoginResponseDto> response = new CustomApiResponse<>(true,ResponseMessages.OTP_SENT_SUCCESS, otpLoginResponseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<CustomApiResponse<VerifyOtpResponseDto>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        VerifyOtpResponseDto verifyOtpResponseDto = oauthService.verifyOtpAndLogin(verifyOtpRequestDto.getPhone(), verifyOtpRequestDto.getOtp());
        String message = verifyOtpResponseDto.isNewUser() ? ResponseMessages.OTP_NEW_USER : ResponseMessages.LOGIN_SUCCESS;
        CustomApiResponse<VerifyOtpResponseDto> response = new CustomApiResponse<>(true, message, verifyOtpResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<CustomApiResponse<LoginResponseDto>> completeProfile(Authentication authentication, HttpServletRequest httpServletRequest, @Valid @RequestBody CompleteProfileRequestDto completeProfileRequestDto) {
        LoginResponseDto loginResponseDto = oauthService.completeProfile(authentication, httpServletRequest, completeProfileRequestDto);
        CustomApiResponse<LoginResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PROFILE_COMPLETED, loginResponseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/google")
    public ResponseEntity<CustomApiResponse<LoginResponseDto>> googleLogin(@RequestBody OAuthLoginRequestDto oAuthLoginRequestDto) {
        LoginResponseDto loginResponseDto = oauthService.googleLogin(oAuthLoginRequestDto);
        CustomApiResponse<LoginResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.LOGIN_SUCCESS, loginResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/google/url")
    public ResponseEntity<CustomApiResponse<OAuthAuthUrlResponseDto>> googleUrl() {
        OAuthAuthUrlResponseDto oAuthAuthUrlResponseDto = oauthService.generateGoogleAuthUrl();
        CustomApiResponse<OAuthAuthUrlResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.GOOGLE_OAUTH_URL, oAuthAuthUrlResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/github")
    public ResponseEntity<CustomApiResponse<LoginResponseDto>> githubLogin(@RequestBody OAuthLoginRequestDto oAuthLoginRequestDto) {
        LoginResponseDto loginResponseDto = oauthService.githubLogin(oAuthLoginRequestDto);
        CustomApiResponse<LoginResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.LOGIN_SUCCESS, loginResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/github/url")
    public ResponseEntity<CustomApiResponse<OAuthAuthUrlResponseDto>> githubUrl() {
        OAuthAuthUrlResponseDto oAuthAuthUrlResponseDto = oauthService.generateGithubAuthUrl();
        CustomApiResponse<OAuthAuthUrlResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.GITHUB_OAUTH_URL, oAuthAuthUrlResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
