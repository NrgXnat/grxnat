package org.nrg.xnat.pogo.events

import com.fasterxml.jackson.annotation.JsonProperty

class EventFilter {

    String name
    @JsonProperty('event-type') String eventType
    @JsonProperty('project-ids') List<String> projectIds
    String status
    @JsonProperty('payload-filter') String payloadFilter
    @JsonProperty('filter-nodes') Map filterNodes

}
