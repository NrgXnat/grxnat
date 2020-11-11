package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
class SwarmConstraint {

    Integer id
    boolean userSettable
    String attribute
    String comparator
    List<String> values

}
