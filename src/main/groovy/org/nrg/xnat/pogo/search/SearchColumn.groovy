package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class SearchColumn {

    String key
    Boolean clickable
    List<LinkProp> linkProps
    String type
    @JsonProperty('xPATH') String xpath
    @JsonProperty('element_name') String elementName
    String header
    String id

}
