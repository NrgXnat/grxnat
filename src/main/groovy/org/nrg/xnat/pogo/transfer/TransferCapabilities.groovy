package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/** {@code GET /xapi/transfer/capabilities}: whether this XNAT build supports a custom Reimport script, and the limits. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class TransferCapabilities {

    /** True when XNAT's importer takes the {@code Anon-Script} parameter (XNAT 1.10.1 and later). */
    boolean perImportAnon
    String anonScriptDialect
    String placeholderSyntax
    Integer manifestMaxRows
    Integer maxAnonScriptBytes
    /** The characters a {@code ${csv.*}} value may use, as the body of a regex character class. */
    String anonValueCharset

}
