package org.nrg.xnat.enums

import org.apache.commons.lang3.StringUtils

enum Handedness {
    RIGHT ("right"),
    LEFT ("left"),
    AMBIDEXTROUS ("ambidextrous"),
    UNKNOWN ("unknown")

    private final String string

    Handedness(String string) {
        this.string = string
    }

    static Handedness get(String value) {
        for (Handedness handedness : values()) {
            if (StringUtils.equalsIgnoreCase(handedness.string, value)) return handedness
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
