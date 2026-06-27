package com.spring.petcareConnect.enums;

import lombok.Getter;

@Getter
public enum ProfileType {
    USER("User"),
    PET("Pet"),
    SPECIALIST("Specialist");

    private final String displayName;

    ProfileType(String displayName) {
        this.displayName = displayName;
    }
}
