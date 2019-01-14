package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty

class Subscription {

    public static final String COMMAND_ACTION_PROVIDER = 'org.nrg.containers.services.CommandActionProvider'

    String name = ''
    boolean active = true
    @JsonProperty('action-key') String actionKey
    Map<String, ?> attributes
    @JsonProperty('event-filter') EventFilter eventFilter
    @JsonProperty('act-as-event-user') boolean actAsEventUser = false

}
