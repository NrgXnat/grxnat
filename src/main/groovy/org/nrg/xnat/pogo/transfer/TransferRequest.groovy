package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * One item of a Batch Transfer batch. The label and value fields apply to Reimport only.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class TransferRequest {

    /** The accession number of the subject, session or assessor to transfer. */
    String id
    TransferMode mode
    String destinationProject
    Boolean preserveSubjectLabel
    Boolean preserveSessionLabel
    String destinationSubjectLabel
    String destinationSessionLabel
    /** Values for the batch script's {@code ${csv.<column>}} placeholders, keyed by column name. */
    Map<String, String> csvValues
    /** The manifest row the item came from, as a spreadsheet numbers it. Recorded in the run's history. */
    Integer rowNumber

    TransferRequest() {}

    TransferRequest(TransferMode mode, String id, String destinationProject) {
        this.mode = mode
        this.id = id
        this.destinationProject = destinationProject
    }

}
