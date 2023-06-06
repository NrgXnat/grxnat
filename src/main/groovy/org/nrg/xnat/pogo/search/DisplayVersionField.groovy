package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class DisplayVersionField {

    String id
    @JsonProperty('element_name') String elementName
    String header
    String type
    Boolean visible
    String value

    @JsonIgnore
    SearchField toSearchField() {
        new SearchField()
                .elementName(elementName)
                .fieldId(id)
                .type(type)
                .header(header)
    }

}
