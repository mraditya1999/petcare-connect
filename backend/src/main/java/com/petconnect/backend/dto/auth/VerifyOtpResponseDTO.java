package com.petconnect.backend.dto.auth;

import com.petconnect.backend.entity.OAuthAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpResponseDTO {
    private Long userId;
    private String email;
    private List<String> roles;
    private String token;
    private String oauthProvider;
    private boolean isProfileComplete;
    private boolean isNewUser;
    private String tempToken;

    public static VerifyOtpResponseDTO forNewUser(String mobileNumber, String tempToken) {
        return new VerifyOtpResponseDTO(
                null, // userId is null for new users at this stage
                null,
                List.of(),
                null,
                OAuthAccount.AuthProvider.MOBILE.name(),
                false,
                true,
                tempToken
        );
    }

    // Factory for existing user
    public static VerifyOtpResponseDTO forExistingUser(UserLoginResponseDTO loginResponse) {
        return new VerifyOtpResponseDTO(
                loginResponse.getUserId(),
                loginResponse.getEmail(),
                loginResponse.getRoles(),
                loginResponse.getToken(),
                loginResponse.getOauthProvider(),
                loginResponse.isProfileComplete(),
                false,
                null
        );
    }
}