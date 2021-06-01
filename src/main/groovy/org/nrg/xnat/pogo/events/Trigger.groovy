package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class Trigger {

    String eventName
    EventStatus status
    String xnatType
    String label
    boolean isXsiType
    @JsonProperty('URI') String uri

}
