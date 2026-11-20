package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonValue

/** The status of a Batch Transfer run, as its history reports it. */
enum TransferRunStatus {

    QUEUED ('Queued'),
    RUNNING ('Running'),
    STOPPING ('Stopping'),
    COMPLETED ('Completed'),
    WARNINGS ('Warnings'),
    FAILED ('Failed'),
    STOPPED ('Stopped')

    TransferRunStatus(String apiValue) {
        this.apiValue = apiValue
    }

    final String apiValue

    @JsonValue
    String getApiValue() {
        apiValue
    }

    boolean isFinished() {
        this in [COMPLETED, WARNINGS, FAILED, STOPPED]
    }

    @Override
    String toString() {
        apiValue
    }

}
