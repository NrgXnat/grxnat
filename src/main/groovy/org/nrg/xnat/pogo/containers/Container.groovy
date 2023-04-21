package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.annotation.JsonProperty

class Container {
    @JsonProperty('id') long databaseId
    String name
    String status
    long commandId
    long wrapperId
    @JsonProperty('env') Map<String, String> environmentVariables
    @JsonProperty("log-paths") List<String> logPaths
}
