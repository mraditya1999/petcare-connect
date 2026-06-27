package com.spring.petcareConnect.dtos.pet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesResponseDto {
    private Long speciesId;
    private String speciesName;
}
