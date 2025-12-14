package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUserDTO {
    private String email;
    private String password;       
    private String firstName;
    private String lastName;
}
