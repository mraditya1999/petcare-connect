package com.petconnect.backend.dto.pet;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetRequestDTO {

    @NotNull(message = "Pet name cannot be null")
    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
    private String petName;

    @NotNull(message = "Breed cannot be null")
    @Size(min = 1, max = 50, message = "Breed must be between 1 and 50 characters")
    private String breed;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age cannot be less than 0")
    @Max(value = 30, message = "Age cannot exceed 30 years")
    private Integer age;

    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "300", message = "Weight cannot exceed 300 kg")
    private Double weight;

    @NotNull(message = "Gender cannot be null")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either 'MALE' or 'FEMALE'")
    private String gender;

    @NotNull(message = "Species cannot be null")
    @Size(min = 1, max = 50, message = "Species must be between 1 and 50 characters")
    private String species;

    private String avatarUrl;
    private String avatarPublicId;
}
