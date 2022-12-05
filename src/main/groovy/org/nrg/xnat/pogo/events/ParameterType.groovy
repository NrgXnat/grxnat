package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonValue

enum ParameterType {

    BOOLEAN,
    STRING,
    TEXT

    @JsonValue
    String jsonRep() {
        name().toLowerCase()
    }

}