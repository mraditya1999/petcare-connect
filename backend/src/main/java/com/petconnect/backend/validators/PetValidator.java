package com.petconnect.backend.validators;

import com.petconnect.backend.dto.PetDTO;
import org.springframework.stereotype.Component;

@Component
public class PetValidator {

    public void validate(PetDTO petDTO) {
        if (petDTO.getPetName() == null || petDTO.getPetName().isEmpty()) {
            throw new IllegalArgumentException("Pet name is required");
        }
        if (petDTO.getAge() == null || petDTO.getAge() < 0) {
            throw new IllegalArgumentException("Invalid age");
        }
        if (petDTO.getWeight() == null || petDTO.getWeight() < 0) {
            throw new IllegalArgumentException("Invalid weight");
        }
    }
}
