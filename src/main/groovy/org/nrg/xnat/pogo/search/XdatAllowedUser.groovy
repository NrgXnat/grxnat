package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class XdatAllowedUser {

    @JacksonXmlProperty(localName = 'xdat:login')
    String login

}
