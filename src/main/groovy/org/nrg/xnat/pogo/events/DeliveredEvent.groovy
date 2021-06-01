package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
@ToString
class DeliveredEvent {

    long id
    String eventType
    String timestamp
    Subscription subscription
    String user
    String project
    Trigger trigger
    List<EventStep> status
    String statusMessage
    Payload payload
    boolean error

}
