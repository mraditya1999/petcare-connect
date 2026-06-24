package com.spring.petcareConnect.dtos.auth.response;

import com.spring.petcareConnect.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
    private String jwtToken;
    private String refreshToken;
    private String accessToken;
    private String tokenType;
    private String oauthProvider;
    private boolean profileComplete;
    private boolean verified;
    private LocalDateTime expiresAt;
    private Long expiresIn;

    public LoginResponseDto(Long userId, String firstName, String lastName, String email,
                            List<String> roles, String accessToken, String oauthProvider,
                            boolean profileComplete, boolean verified) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.oauthProvider = oauthProvider;
        this.profileComplete = profileComplete;
        this.verified = verified;
    }

}
