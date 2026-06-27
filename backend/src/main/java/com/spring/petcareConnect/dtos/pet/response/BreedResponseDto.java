package com.spring.petcareConnect.dtos.pet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreedResponseDto {
    private Long breedId;
    private String breedName;
    private SpeciesResponseDto species;
}
