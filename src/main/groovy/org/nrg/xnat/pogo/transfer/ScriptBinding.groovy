package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/** Whether every {@code ${csv.*}} placeholder in a script has a manifest column. {@code unbound} lists those that don't. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class ScriptBinding {

    boolean bound
    List<String> unbound = []

}
