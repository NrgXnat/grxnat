package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * {@code POST /xapi/transfer/validate/manifest}. The plugin answers 200 when required columns are missing or rows
 * aren't found; only an unreadable or oversized manifest is an error status. {@code summary} is present once rows are
 * resolved, and {@code scriptBinding} only when the request included a script.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class ManifestValidationResult {

    List<String> columns = []
    boolean requiredPresent
    List<String> missingColumns = []
    List<String> reservedColumns = []
    List<String> routingColumns = []
    List<String> valueColumns = []
    int totalRows
    ManifestSummary summary
    List<ManifestRow> rows = []
    ScriptBinding scriptBinding

}
