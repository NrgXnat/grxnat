package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

@JsonInclude(JsonInclude.Include.NON_NULL)
class ConfigServiceObject {

    String path
    String reason
    String contents
    String project
    Boolean unversioned
    @JsonProperty('create_date') String createDate
    String user
    int version
    String tool
    Status status

    enum Status {
        ENABLED,
        STATUS

        @JsonValue
        @Override
        String toString() {
            name().toLowerCase()
        }
    }

}
