package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class EventFilter {

    String name
    String eventType
    List<String> projectIds
    EventStatus status
    String payloadFilter
    String schedule
    String scheduleDescription

}
