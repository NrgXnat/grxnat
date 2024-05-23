package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum VariableType {
    INTEGER,
    FLOAT,
    DATE,
    STRING,
    BOOLEAN

    @Override
    @JsonValue
    String toString() {
        name().toLowerCase()
    }

}
