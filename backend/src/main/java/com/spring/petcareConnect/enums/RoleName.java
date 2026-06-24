package com.spring.petcareConnect.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_ADMIN("Administrator"),
    ROLE_SPECIALIST("Specialist"),
    ROLE_USER("User");

    private final String displayName;

    RoleName(String displayName) {
        this.displayName = displayName;
    }

}