package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class SearchWhere extends SearchContainer {

    @JacksonXmlProperty(isAttribute = true)
    SearchMethod method

}
