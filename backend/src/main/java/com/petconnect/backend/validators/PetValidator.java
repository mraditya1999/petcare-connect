package com.petconnect.backend.validators;

import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.exceptions.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validator for Pet DTOs.
 * Ensures pet request data meets business requirements.
 */
@Component
public class PetValidator extends BaseValidator {

    public void validate(PetRequestDTO petRequestDTO) {
        requireNotNull(petRequestDTO, "Pet request");
        requireNotBlank(petRequestDTO.getPetName(), "Pet name");

        requireTrue(petRequestDTO.getAge() >= 0, "Pet age cannot be negative");

        if (petRequestDTO.getWeight() != null) {
            requireTrue(petRequestDTO.getWeight() >= 0, "Pet weight cannot be negative");
        }
    }
}
