package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty

class Subscription {

    public static final String COMMAND_ACTION_PROVIDER = 'org.nrg.containers.services.CommandActionProvider'

    @JsonProperty('act-as-event-user') boolean actAsEventUser = false
    @JsonProperty('action-key') String actionKey
    boolean active = true
    Map attributes
    @JsonProperty('event-filter') Map<String, String> eventFilter
    @JsonProperty('event-id') String eventId
    String name = ''
    @JsonProperty('project-id') String projectId

}
