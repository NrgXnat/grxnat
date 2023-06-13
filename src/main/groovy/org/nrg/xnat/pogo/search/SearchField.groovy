package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.pogo.DataType

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
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

    SearchField elementName(DataType dataType) {
        elementName(dataType.xsiType)
    }

    @JsonIgnore
    SearchColumn generateExpectedSearchColumn(boolean isDisplayField, boolean matchesRootDataType) {
        final String dataTypeAsPrefix = matchesRootDataType ? '' : elementName.toLowerCase().replace(':', '_') + '_'
        final String mainKeyComponent = (isDisplayField ? fieldId : fieldId.replace('/', '').replace(':', '_col_')).toLowerCase()

        new SearchColumn()
                .key([dataTypeAsPrefix, mainKeyComponent].join('').replace('=', '_'))
                .type(type)
                .xpath("${elementName}.${mainKeyComponent.toUpperCase().split('=')[0]}")
                .elementName(elementName)
                .header(header)
                .id(mainKeyComponent.toUpperCase())
    }

}
