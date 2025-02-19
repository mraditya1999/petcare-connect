package com.petconnect.backend.validators;

import com.petconnect.backend.dto.pet.PetRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class PetValidator {

    public void validate(PetRequestDTO petRequestDTO) {
        if (petRequestDTO.getPetName() == null || petRequestDTO.getPetName().isEmpty()) {
            throw new IllegalArgumentException("Pet name is required");
        }
        if (petRequestDTO.getAge() < 0) {
            throw new IllegalArgumentException("Invalid age");
        }
        if (petRequestDTO.getWeight() == null || petRequestDTO.getWeight() < 0) {
            throw new IllegalArgumentException("Invalid weight");
        }
    }
}
