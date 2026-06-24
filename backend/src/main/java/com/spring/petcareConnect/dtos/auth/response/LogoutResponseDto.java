package com.spring.petcareConnect.dtos.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDto {
    private Long userId;
    private boolean sessionEnded;
    private String revokedTokenType;
}
