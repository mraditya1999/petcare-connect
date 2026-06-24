package com.spring.petcareConnect.dtos.oauth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginRequestDto {

    @NotBlank(message = "Authorization code is required")
    private String code;

    @NotBlank(message = "State is required")
    private String state;
}
