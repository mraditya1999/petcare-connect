package com.spring.petcareConnect.dtos.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponseDto {
    private String email;
    private boolean passwordReset;
}
