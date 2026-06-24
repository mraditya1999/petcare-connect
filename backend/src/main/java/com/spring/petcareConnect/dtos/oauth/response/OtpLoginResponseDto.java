package com.spring.petcareConnect.dtos.oauth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginResponseDto {
    private String phone;
}
