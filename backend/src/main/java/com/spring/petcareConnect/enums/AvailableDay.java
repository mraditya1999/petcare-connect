package com.spring.petcareConnect.enums;

import lombok.Getter;

@Getter
public enum AvailableDay {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String displayName;

    AvailableDay(String displayName) {
        this.displayName = displayName;
    }
}
