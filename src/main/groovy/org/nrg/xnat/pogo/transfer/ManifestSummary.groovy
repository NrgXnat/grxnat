package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/** How a manifest's rows resolved. {@code matched} counts sessions; the other counts are rows. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class ManifestSummary {

    int matched
    int notFound
    int noSessions
    int duplicateSession

}
