package com.petconnect.backend.dto.pet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDTO {

    private Long petId;

    @NotNull(message = "Pet name cannot be null")
    @Size(min = 1, max = 255, message = "Pet name must be between 1 and 255 characters")
    private String petName;

    @NotNull(message = "Age cannot be null")
    @Positive(message = "Age must be a positive number")
    private Integer age;

    @NotNull(message = "Weight cannot be null")
    @Positive(message = "Weight must be a positive number")
    private Double weight;

    private String avatarUrl;
    private String avatarPublicId;

    @NotNull(message = "Gender cannot be null")
    @Size(min = 1, max = 10, message = "Gender must be between 1 and 10 characters")
    private String gender;

    @NotNull(message = "Breed cannot be null")
    @Size(min = 1, max = 255, message = "Breed must be between 1 and 255 characters")
    private String breed;

    @NotNull(message = "Species cannot be null")
    @Size(min = 1, max = 255, message = "Species must be between 1 and 255 characters")
    private String species;
}
