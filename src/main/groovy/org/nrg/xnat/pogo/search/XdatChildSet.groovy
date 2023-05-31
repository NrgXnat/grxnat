package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XdatChildSet extends SearchContainer<XdatChildSet> implements SearchCriterion {

    @JacksonXmlProperty(isAttribute = true)
    SearchMethod method

}
