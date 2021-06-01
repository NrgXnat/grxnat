package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum PaginatedApiSortDirection {

    ASCENDING ('asc'),
    DESCENDING ('desc')

    final String jsonRep

    PaginatedApiSortDirection(String jsonRep) {
        this.jsonRep = jsonRep
    }

    @JsonValue
    String jsonRepresentation() {
        jsonRep
    }

}