package com.spring.petcareConnect.dtos.oauth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAuthUrlResponseDto {
    private String provider;
    private String url;
    private String redirectUri;
    private String state;
}
