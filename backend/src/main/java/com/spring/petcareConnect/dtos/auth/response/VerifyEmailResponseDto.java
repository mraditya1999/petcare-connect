package com.spring.petcareConnect.dtos.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.petcareConnect.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyEmailResponseDto {

    private boolean verified;
    private List<RoleName> roles;
    private boolean isAdmin;

}