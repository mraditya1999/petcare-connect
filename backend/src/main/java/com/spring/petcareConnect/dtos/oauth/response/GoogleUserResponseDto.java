package com.spring.petcareConnect.dtos.oauth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserResponseDto {
    private String sub;
    private String email;
    private String given_name;
    private String family_name;
    private String picture;
}
