package com.spring.petcareConnect.dtos.specialist.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecialistResponseDto {

    private Long specialistId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String about;
    private boolean verified;
    private boolean available;
    private LocalDateTime createdAt;

    public SpecialistResponseDto(Long specialistId, String firstName, String lastName, String about, boolean verified) {
        this.specialistId = specialistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.about = about;
        this.verified = verified;
    }
}