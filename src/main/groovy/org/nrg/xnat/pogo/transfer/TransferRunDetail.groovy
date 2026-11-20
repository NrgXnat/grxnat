package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * {@code GET /xapi/transfer/history/{tracking_id}}: a run and its items. {@code anonScript} is present only for users
 * who may see the run's script. Pass {@code itemsAsOf} as {@code changed_since} to get only the items changed since.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransferRunDetail {

    TransferRun run
    boolean canStop
    String anonScript
    List<TransferRunItem> items = []
    Long itemsAsOf

    /** The item for the given source accession number, or null. */
    TransferRunItem findItem(String itemId) {
        items.find { it.itemId == itemId }
    }

    List<TransferRunItem> itemsWithStatus(TransferItemStatus status) {
        items.findAll { it.status == status }
    }

    /** The run's status and each item's outcome, for assertion messages. */
    String summary() {
        final List<String> lines = ["Transfer run ${run?.trackingId} is ${run?.status}: ${run?.passedCount} passed, ${run?.failedCount} failed, ${run?.stoppedCount} stopped of ${run?.itemCount}.".toString()]
        items.each { item ->
            lines << "  ${item.mode} ${item.itemId} -> ${item.destinationProject}: ${item.status}${item.message ? " (${item.message})" : ''}".toString()
        }
        lines.join('\n')
    }

}
