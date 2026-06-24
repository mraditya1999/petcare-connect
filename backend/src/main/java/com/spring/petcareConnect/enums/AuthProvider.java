package com.spring.petcareConnect.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("Google"),
    GITHUB("GitHub"),
    MOBILE("Mobile"),
    LOCAL("Local");

    private final String displayName;

    AuthProvider(String displayName) {
        this.displayName = displayName;
    }

}