package org.example.model;

public enum Gender {
    MALE,
    FEMALE,
    ;

    public static Gender fromString(String name) {
        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(name))
                return gender;
        }

        return null;
    }
}