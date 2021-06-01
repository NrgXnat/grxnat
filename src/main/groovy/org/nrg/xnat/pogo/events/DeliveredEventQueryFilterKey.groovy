package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonValue

enum DeliveredEventQueryFilterKey {

    EVENTTYPE,
    PROJECT,
    STATUS,
    SUBSCRIPTION,
    USER

    @JsonValue
    String lowercase() {
        name().toLowerCase()
    }

}