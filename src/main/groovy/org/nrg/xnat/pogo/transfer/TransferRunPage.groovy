package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/** {@code GET /xapi/transfer/history}: one page of runs, newest first. {@code page} counts from 0. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransferRunPage {

    List<TransferRun> runs = []
    long total
    int page
    int size

}
