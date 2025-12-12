package com.petconnect.backend.dto;

import com.petconnect.backend.dto.auth.UserLoginResponseDTO;

public class VerifyOtpResult {
    private final boolean isNewUser;
    private final UserLoginResponseDTO loginResponse;
    private Long userId;
    private String tempToken;

    public VerifyOtpResult(boolean isNewUser, UserLoginResponseDTO loginResponse) {
        this.isNewUser = isNewUser;
        this.loginResponse = loginResponse;
    }

    public VerifyOtpResult(boolean isNewUser, UserLoginResponseDTO loginResponse, Long userId, String tempToken) {
        this.isNewUser = isNewUser;
        this.loginResponse = loginResponse;
        this.userId = userId;
        this.tempToken = tempToken;
    }

    public boolean isNewUser() { return isNewUser; }
    public UserLoginResponseDTO getLoginResponse() { return loginResponse; }
    public Long getUserId() { return userId; }
    public String getTempToken() { return tempToken; }
}
