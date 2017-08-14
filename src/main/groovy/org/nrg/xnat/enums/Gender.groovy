package org.nrg.xnat.enums

import org.apache.commons.lang3.StringUtils

enum Gender {
    MALE ("male"),
    FEMALE ("female"),
    UNKNOWN ("unknown")

    private final String string

    Gender(String string) {
        this.string = string
    }

    static Gender get(String value) {
        for (Gender gender : values()) {
            if (StringUtils.equalsIgnoreCase(gender.string, value)) return gender
        }
        return null
    }

    String capitalize() {
        return StringUtils.capitalize(string)
    }

    @Override
    String toString() {
        return string
    }
}
