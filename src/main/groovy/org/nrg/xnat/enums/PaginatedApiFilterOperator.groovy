package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum PaginatedApiFilterOperator {

    LIKE

    @JsonValue
    String lowercase() {
        name().toLowerCase()
    }

}