package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonValue

/** How a Batch Transfer item is transferred: the plugin's {@code mode} field. */
enum TransferMode {

    SHARE ('Share'),
    CLONE ('Clone'),
    REIMPORT ('Reimport')

    TransferMode(String apiValue) {
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
