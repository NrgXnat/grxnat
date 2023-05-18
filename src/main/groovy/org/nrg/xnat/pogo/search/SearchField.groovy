package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class SearchField {

    @JacksonXmlProperty(localName = 'xdat:element_name')
    String elementName

    @JacksonXmlProperty(localName = 'xdat:field_ID')
    String fieldId

    @JacksonXmlProperty(localName = 'xdat:sequence')
    Integer sequence

    @JacksonXmlProperty(localName = 'xdat:type')
    String type

    @JacksonXmlProperty(localName = 'xdat:header')
    String header

    @JacksonXmlProperty(localName = 'xdat:value')
    String value

}
