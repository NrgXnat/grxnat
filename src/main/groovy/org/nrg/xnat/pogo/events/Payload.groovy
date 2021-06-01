package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Payload {

    String id
    String label
    String xsiType
    String uri
    @JsonProperty('project-id') String projectId

}
