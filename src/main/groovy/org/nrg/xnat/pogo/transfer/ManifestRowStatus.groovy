package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonValue

/** How a manifest row resolved against the source project. Only a matched row can be transferred. */
enum ManifestRowStatus {

    MATCHED ('matched'),
    SUBJECT_NOT_FOUND ('subject_not_found'),
    SESSION_NOT_FOUND ('session_not_found'),
    NO_SESSIONS ('no_sessions'),
    DUPLICATE_SESSION ('duplicate_session')

    ManifestRowStatus(String apiValue) {
        this.apiValue = apiValue
    }

    final String apiValue

    @JsonValue
    String getApiValue() {
        apiValue
    }

    @Override
    String toString() {
        apiValue
    }

}
