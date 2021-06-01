package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum QCStatus {
    FAIL (0),
    PASS (1)

    QCStatus(int representation) {
        xnatRepresentation = representation
    }

    int xnatRepresentation

    @JsonValue
    int toValue() {
        xnatRepresentation
    }
}