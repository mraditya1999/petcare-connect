package com.petconnect.backend.dto;

import com.petconnect.backend.dto.auth.UserLoginResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyOtpResult {
    private final boolean isNewUser;
    private final UserLoginResponseDTO loginResponse;
    private Long userId;
    private String tempToken;

    // Convenience constructor for new user case
    public VerifyOtpResult(boolean isNewUser, UserLoginResponseDTO loginResponse) {
        this(isNewUser, loginResponse, null, null);
    }
}
