package com.spring.petcareConnect.dtos.profile.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDto {
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName; // optional

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName; // optional

    @Size(min = 10, max = 20, message = "Mobile number must be between 10 and 20 characters")
    private String mobileNumber; // optional, must be unique if provided

    @Valid
    private AddressDto addressDto; // optional, but validated if provided
}
