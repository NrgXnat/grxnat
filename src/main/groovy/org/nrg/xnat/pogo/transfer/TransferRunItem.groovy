package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * One item of a Batch Transfer run. {@code csvValues} is present only for users who may see the run's values. For a
 * user who can't read the destination project, the destination fields are left out and {@code destinationHidden} is
 * true.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransferRunItem {

    /** The source item's accession number. */
    String itemId
    TransferMode mode
    Integer rowNumber
    String sourceSubjectLabel
    String sourceLabel
    String destinationProject
    String destinationSubjectLabel
    String destinationSessionLabel
    Boolean preserveSubjectLabel
    Boolean preserveSessionLabel
    Map<String, String> csvValues
    TransferItemStatus status
    String message
    /** The accession number of the item's copy at the destination, once there is one. */
    String destinationId
    String destinationLabel
    Boolean destinationHidden
    Date started
    Date finished

}
