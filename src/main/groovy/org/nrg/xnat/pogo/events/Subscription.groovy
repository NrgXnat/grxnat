package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import groovy.transform.EqualsAndHashCode

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
@EqualsAndHashCode
class Subscription {

    public static final String COMMAND_ACTION_PROVIDER = 'org.nrg.containers.services.CommandActionProvider'

    Integer id
    String name = ''
    boolean active = true
    String actionKey
    Map<String, ?> attributes
    EventFilter eventFilter
    boolean actAsEventUser = false
    String subscriptionOwner

}