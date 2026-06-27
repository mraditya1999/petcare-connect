package com.spring.petcareConnect.dtos.pet.request;

import com.spring.petcareConnect.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetRequestDto {

    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
    private String petName;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Pet age cannot be negative")
    @Max(value = 30, message = "Pet age cannot exceed 30 years")
    private Integer age;

    @DecimalMin(value = "0.0", inclusive = true, message = "Pet weight cannot be negative")
    @DecimalMax(value = "300", message = "Pet weight cannot exceed 300 kg")
    private Double weight;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Breed ID is required")
    @Positive
    private Long breed;

    private String avatarUrl;
    private String avatarPublicId;
}
