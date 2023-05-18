package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import io.restassured.internal.mapping.Jackson2Mapper
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import java.lang.reflect.Type

class XnatXmlMapper extends XmlMapper {

    private static final XMLInputFactory INPUT_FACTORY = inputFactory()
    private static final XMLOutputFactory OUTPUT_FACTORY = outputFactory()
    public static final XnatXmlMapper INSTANCE = new XnatXmlMapper()
    public static final Jackson2Mapper REST_MAPPER = new Jackson2Mapper(new Jackson2ObjectMapperFactory() {
        @Override
        ObjectMapper create(Type type, String s) {
            INSTANCE
        }
    })

    XnatXmlMapper() {
        super(INPUT_FACTORY, OUTPUT_FACTORY)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private static XMLInputFactory inputFactory() {
        final XMLInputFactory input = XMLInputFactory.newInstance()
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false)
        input
    }

    private static XMLOutputFactory outputFactory() {
        final XMLOutputFactory output = XMLOutputFactory.newFactory()
        output.setProperty('com.ctc.wstx.useDoubleQuotesInXmlDecl', true)
        output
    }

}
