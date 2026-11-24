package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * The JSON body of {@code POST /xapi/transfer/validate/manifest}. A manifest always resolves for Reimport, so the
 * plugin ignores {@code mode}. With {@code anonScript}, the result reports which placeholders the manifest's columns
 * bind. {@code fileCount} adds each matched session's DICOM file count, at the cost of a walk of its files.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ManifestValidationRequest {

    String sourceProject
    TransferMode mode
    String manifestCsv
    String anonScript
    Boolean fileCount

}
