package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty

class SearchElement {

    @JsonProperty('SINGULAR') String singularName
    @JsonProperty('PLURAL') String pluralName
    @JsonProperty('SECURED') boolean secured
    @JsonProperty('ELEMENT_NAME') String elementName
    @JsonProperty('COUNT') int count

}
