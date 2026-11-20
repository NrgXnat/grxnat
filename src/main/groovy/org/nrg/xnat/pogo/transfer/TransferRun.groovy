package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * A Batch Transfer run, as its history reports it. The counts are live while the run is unfinished and final after.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransferRun {

    String trackingId
    TransferRunStatus status
    /** The items' mode ({@code Share}, {@code Clone} or {@code Reimport}), or {@code Mixed}. */
    String operation
    String sourceProject
    /** Null when the items go to more than one project. */
    String destinationProject
    int itemCount
    int passedCount
    int failedCount
    int stoppedCount
    boolean customScript
    boolean anonReplacedPipeline
    Boolean preserveSubjectLabel
    Boolean preserveSessionLabel
    String createdBy
    Date created
    Date finished
    String stopRequestedBy
    Date stopRequestedAt

}
