package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * {@code POST /xapi/transfer/validate/script}. The plugin answers 200 whether or not the script is valid. {@code binding}
 * is present only when the request gave manifest columns.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class ScriptValidationResult {

    boolean valid
    String dialect
    int sizeBytes
    List<String> placeholders = []
    List<String> errors = []
    List<String> restrictionViolations = []
    ScriptBinding binding
    String message

}
