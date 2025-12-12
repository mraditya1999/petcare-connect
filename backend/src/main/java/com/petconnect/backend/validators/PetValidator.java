package com.petconnect.backend.validators;

import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.exceptions.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validator for Pet DTOs.
 * Ensures pet request data meets business requirements.
 */
@Component
public class PetValidator {

    public void validate(PetRequestDTO petRequestDTO) {
        if (petRequestDTO == null) {
            throw new ValidationException("Pet request cannot be null");
        }
        if (petRequestDTO.getPetName() == null || petRequestDTO.getPetName().isBlank()) {
            throw new ValidationException("Pet name is required and cannot be empty");
        }
        if (petRequestDTO.getAge() < 0) {
            throw new ValidationException("Pet age cannot be negative");
        }
        if (petRequestDTO.getWeight() != null && petRequestDTO.getWeight() < 0) {
            throw new ValidationException("Pet weight cannot be negative");
        }
    }
}
