package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonValue

/** The status of one item in a Batch Transfer run. */
enum TransferItemStatus {

    QUEUED ('Queued'),
    RUNNING ('Running'),
    DONE ('Done'),
    FAILED ('Failed'),
    STOPPED ('Stopped')

    TransferItemStatus(String apiValue) {
        this.apiValue = apiValue
    }

    final String apiValue

    @JsonValue
    String getApiValue() {
        apiValue
    }

    boolean isFinished() {
        this in [DONE, FAILED, STOPPED]
    }

    @Override
    String toString() {
        apiValue
    }

}
