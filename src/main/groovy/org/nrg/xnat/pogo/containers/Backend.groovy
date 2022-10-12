package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum Backend {
    DOCKER,
    SWARM,
    KUBERNETES

    @JsonValue
    String lower() {
        return name().toLowerCase()
    }

    @JsonCreator
    static Backend fromString(final String value) {
        return value == null ? null : valueOf(value.toUpperCase())
    }
}