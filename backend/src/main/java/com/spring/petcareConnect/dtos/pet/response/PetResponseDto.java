package com.spring.petcareConnect.dtos.pet.response;

import com.spring.petcareConnect.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDto {

    private Long petId;
    private String petName;
    private Integer age;
    private Double weight;
    private String avatarUrl;
    private String avatarPublicId;
    private Gender gender;
    private BreedResponseDto breed;
}
