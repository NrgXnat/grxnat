package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
class KubernetesToleration {

    Integer id
    String key
    String operator
    String value
    String effect

}
