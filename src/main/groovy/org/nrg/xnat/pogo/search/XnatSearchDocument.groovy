package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.nrg.xnat.jackson.deserializers.StringIntBooleanDeserializer
import org.nrg.xnat.jackson.mappers.XnatXmlMapper
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.users.User

@EqualsAndHashCode
@Builder(builderStrategy = SimpleStrategy, prefix = '')
@JacksonXmlRootElement(localName = 'xdat:bundle')
class XnatSearchDocument {

    @JacksonXmlProperty(isAttribute = true, localName = 'xmlns:xdat')
    String namespace

    @JacksonXmlProperty(isAttribute = true, localName = 'ID')
    String id

    @JacksonXmlProperty(isAttribute = true, localName = 'allow-diff-columns')
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonDeserialize(using = StringIntBooleanDeserializer)
    Boolean allowDiffColumns

    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonDeserialize(using = StringIntBooleanDeserializer)
    Boolean secure

    @JacksonXmlProperty(isAttribute = true, localName = 'brief-description')
    String briefDescription

    @JacksonXmlProperty(isAttribute = true)
    String tag

    @JacksonXmlProperty(localName = 'xdat:root_element_name')
    String rootElementName

    @JacksonXmlProperty(localName = 'xdat:search_field')
    @JacksonXmlElementWrapper(useWrapping = false)
    List<SearchField> searchFields = []

    @JacksonXmlProperty(localName = 'xdat:search_where')
    SearchWhere searchWhere

    @JacksonXmlProperty(localName = 'xdat:allowed_user')
    @JacksonXmlElementWrapper(useWrapping = false)
    List<XdatAllowedUser> allowedUsers = []

    XnatSearchDocument addSearchField(SearchField searchField) {
        searchFields << searchField.sequence(allocateNextSequence())
        this
    }

    XnatSearchDocument addSearchField(DataType dataType, String fieldId, String type, String header) {
        addSearchField(
                new SearchField(
                        elementName: dataType.xsiType,
                        fieldId: fieldId,
                        type: type,
                        header: header
                )
        )
    }

    XnatSearchDocument addSearchCriterion(SearchCriterion searchCriterion) {
        if (searchWhere == null) {
            searchWhere = new SearchWhere(method: SearchMethod.AND)
        }
        searchWhere.addSearchCriterion(searchCriterion)
        this
    }

    XnatSearchDocument correctSchemaFields() {
        if (searchWhere) {
            searchWhere.findRecursiveCriteria().each { xdatCriteria ->
                final String schemaField = xdatCriteria.schemaField
                if (schemaField) {
                    xdatCriteria.setSchemaField(schemaField.replaceAll('/PROJECT$', '/project'))
                }
            }
        }

        this
    }

    XnatSearchDocument addAllowedUser(User user) {
        allowedUsers << new XdatAllowedUser(login: user.username)
        this
    }

    @JsonIgnore
    List<String> getAllowedUsersAsStrings() {
        allowedUsers*.login
    }

    String asString() {
        XnatXmlMapper.INSTANCE.writeValueAsString(this)
    }

    private int allocateNextSequence() {
        (searchFields == null ? 0 : searchFields*.sequence.max()) + 1
    }

    static XnatSearchDocument fromXml(File xml) {
        fromXml(xml.text)
    }

    static XnatSearchDocument fromXml(String xmlString) {
        XnatXmlMapper.INSTANCE.readValue(xmlString, XnatSearchDocument)
    }

}
