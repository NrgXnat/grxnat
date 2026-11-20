package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * One manifest row, or one session resolved from it. A subject row (no source session label) that matches is replaced
 * by one entry per image session, each keeping the row's {@code index} and {@code rowNumber}.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class ManifestRow {

    /** 1-based position among the manifest's data rows. */
    int index
    /** The row's number as a spreadsheet shows the file, counting the header as row 1. */
    int rowNumber
    String sourceSubjectLabel
    String sourceSessionLabel
    boolean subjectRow
    String destinationSubjectLabel
    String destinationSessionLabel
    ManifestRowStatus status
    /** The matched session's accession number. */
    String resolvedId
    /** Present only when the request asked for file counts. */
    Integer fileCount
    Map<String, String> csvValues = [:]
    /** Values outside the allowed characters. A row with any can't be submitted. */
    List<String> valueErrors = []
    List<String> valueWarnings = []

}
