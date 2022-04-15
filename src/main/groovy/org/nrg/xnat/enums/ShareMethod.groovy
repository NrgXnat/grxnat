package org.nrg.xnat.enums

import com.fasterxml.jackson.annotation.JsonValue

enum ShareMethod {

    STANDARD_SHARE ('Share'),
    COPY ('Copy')

    ShareMethod(String apiValue) {
        apiVal = apiValue
    }

    final String apiVal

    @JsonValue
    String getApiRepresentation() {
        apiVal
    }

}
