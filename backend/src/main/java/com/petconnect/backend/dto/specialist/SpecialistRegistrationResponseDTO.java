package com.petconnect.backend.dto.specialist;

public class SpecialistRegistrationResponseDTO {
    private String message;

    public SpecialistRegistrationResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

