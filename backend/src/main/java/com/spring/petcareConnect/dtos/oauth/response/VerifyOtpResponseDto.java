package com.spring.petcareConnect.dtos.oauth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.petcareConnect.dtos.auth.response.LoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpResponseDto {
    private String accessToken;
    private String email;
    private String firstName;
    private String lastName;
    private String oauthProvider;
    private boolean profileComplete;
    private List<String> roles;
    private String tokenType;
    private Long userId;
    private boolean verified;
    private boolean newUser;

    public static VerifyOtpResponseDto forExistingUser(LoginResponseDto login) {
        return new VerifyOtpResponseDto(
                login.getAccessToken(),
                login.getEmail(),
                login.getFirstName(),
                login.getLastName(),
                login.getOauthProvider(),
                login.isProfileComplete(),
                login.getRoles(),
                login.getTokenType(),
                login.getUserId(),
                login.isVerified(),
                false
        );
    }

    public static VerifyOtpResponseDto forNewUser(String phone, String tempToken) {
        VerifyOtpResponseDto dto = new VerifyOtpResponseDto();
        dto.setAccessToken(tempToken);
        dto.setOauthProvider("MOBILE");
        dto.setNewUser(true);
        dto.setVerified(false);
        dto.setProfileComplete(false);
        return dto;
    }
}
