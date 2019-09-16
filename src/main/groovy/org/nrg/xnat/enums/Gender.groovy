package org.nrg.xnat.enums

import org.apache.commons.lang3.StringUtils

enum Gender {
    MALE ('male'),
    FEMALE ('female'),
    UNKNOWN ('unknown')

    private final String string

    Gender(String string) {
        this.string = string
    }

    static Gender get(String value) {
        values().find { gender ->
            StringUtils.equalsIgnoreCase(gender.string, value)
        }
    }

    String capitalize() {
        StringUtils.capitalize(string)
    }

    @Override
    String toString() {
        string
    }
}
