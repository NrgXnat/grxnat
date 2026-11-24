package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * The body of {@code POST /xapi/transfer}. The plugin accepts {@code anonScript} only when every item is a Reimport,
 * and applies it to each one. {@code anonReplacePipeline} makes the script replace the destination's own
 * anonymization rather than run as well as it, and has no effect without a script.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BatchTransferPayload {

    public static final String TRACKING_ID_PREFIX = 'batch_transfer_'

    String trackingId
    /** The project the transfer is started from, by ID or alias. The run's history is recorded under it. */
    String sourceProject
    String anonScript
    Boolean anonReplacePipeline
    List<TransferRequest> requests = []

    BatchTransferPayload addRequest(TransferRequest request) {
        requests << request
        this
    }

    /** A new tracking ID, in the form the plugin's own screen uses. */
    static String newTrackingId() {
        "${TRACKING_ID_PREFIX}${UUID.randomUUID()}".toString()
    }

}
