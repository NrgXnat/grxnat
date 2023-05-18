package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.jackson.deserializers.StringIntBooleanDeserializer

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
class XdatCriteria implements SearchCriterion {

    @JacksonXmlProperty(isAttribute = true, localName = 'override_value_formatting')
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonDeserialize(using = StringIntBooleanDeserializer)
    Boolean overrideValueFormatting

    @JacksonXmlProperty(localName = 'xdat:schema_field')
    String schemaField

    @JacksonXmlProperty(localName = 'xdat:comparison_type')
    ComparisonType comparisonType

    @JacksonXmlProperty(localName = 'xdat:value')
    String value

    @JacksonXmlProperty(localName = 'xdat:custom_search')
    String customSearch

}
