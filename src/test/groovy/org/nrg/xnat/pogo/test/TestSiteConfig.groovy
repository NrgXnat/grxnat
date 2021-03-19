package org.nrg.xnat.pogo.test

import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.xnat.pogo.SiteConfig
import org.nrg.xnat.pogo.SiteConfigProps
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestSiteConfig {

    @Test
    void testSiteConfigSerializationRoundTrip() {
        final String timeout = '15 minutes'
        final String unknownProperty = 'other_property'
        final Integer unknownValue = 1000
        final SiteConfig initialConfig = new SiteConfig(sessionTimeout: timeout, untrackedProperties: [(unknownProperty) : unknownValue])
        final ObjectMapper objectMapper = new ObjectMapper()
        final String asJson = objectMapper.writeValueAsString(initialConfig)
        assertEquals("{\"${SiteConfigProps.SESSION_TIMEOUT}\":\"${timeout}\",\"${unknownProperty}\":${unknownValue}}".toString(), asJson)
        final SiteConfig roundTrip = objectMapper.readValue(asJson, SiteConfig)
        assertEquals(timeout, roundTrip.sessionTimeout)
        assertEquals(unknownValue, roundTrip.untrackedProperties[unknownProperty])
    }

}
