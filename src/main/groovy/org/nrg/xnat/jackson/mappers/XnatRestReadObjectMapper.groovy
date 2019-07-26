package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.xnat.jackson.modules.XnatRestReadDeserializationModule

class XnatRestReadObjectMapper extends ObjectMapper {

    XnatRestReadObjectMapper(boolean strict = true) {
        super()
        if (!strict) {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        }
        registerModule(new XnatRestReadDeserializationModule())
    }

}
