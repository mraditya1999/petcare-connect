package com.spring.petcareConnect.dtos.specialist.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.petcareConnect.enums.AvailableDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

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
    private Integer slotDuration;
    private String specialization;
    private Integer experienceYears;
    private Double rating;
    private BigDecimal consultationFee;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private Set<AvailableDay> daysAvailable;
    private String location;

    public SpecialistResponseDto(Long specialistId, String firstName, String lastName, String about, boolean verified) {
        this.specialistId = specialistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.about = about;
        this.verified = verified;
    }
}