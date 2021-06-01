package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class Action {

    public static final String LOGGING_ACTION = 'org.nrg.xnat.eventservice.actions.EventServiceLoggingAction:org.nrg.xnat.eventservice.actions.EventServiceLoggingAction'

    String actionKey
    String displayName
    String description
    Map<String, ActionParameter> attributes = [:]

}
