package org.nrg.xnat.pogo.events

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class EventStep {

    EventStepStatus status
    String timestamp
    String message

}
