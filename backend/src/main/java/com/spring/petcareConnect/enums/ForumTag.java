package com.spring.petcareConnect.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ForumTag {
    HEALTH("Health"),
    TRAINING("Training"),
    ADOPTION("Adoption"),
    SUPPORT("Support"),
    GENERAL("General");

    private final String displayName;

    ForumTag(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static ForumTag fromString(String value) {
        for (ForumTag tag : ForumTag.values()) {
            if (tag.name().equalsIgnoreCase(value) || tag.displayName.equalsIgnoreCase(value)) {
                return tag;
            }
        }
        throw new IllegalArgumentException(
                "Invalid ForumTag value: '" + value + "'. Allowed values are: HEALTH, TRAINING, ADOPTION, SUPPORT, GENERAL"
        );
    }
}
