package org.nrg.xnat.enums

import org.apache.commons.lang3.StringUtils

enum Accessibility {
    PUBLIC ('public'),
    PROTECTED ('protected'),
    PRIVATE ('private')

    private final String string

    Accessibility(String string) {
        this.string = string
    }

    static Accessibility get(String value) {
        for (Accessibility accessibility : values()) {
            if (StringUtils.equalsIgnoreCase(accessibility.string, value)) return accessibility
        }
        null
    }

    @Override
    String toString() {
        string
    }

}
