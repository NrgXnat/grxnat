package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class ActionParameter {

    String description
    ParameterType type
    String defaultValue
    boolean required

}
