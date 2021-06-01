package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonValue

enum DeliveredEventQuerySortColumn {

    STATUS,
    TIMESTAMP,
    USER

    @JsonValue
    String lowercase() {
        name().toLowerCase()
    }

}