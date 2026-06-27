package com.spring.petcareConnect.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public static Gender fromString(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(value) || gender.displayName.equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender value: '" + value + "'. Allowed values are: MALE, FEMALE");
    }
}