package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.xnat.jackson.modules.XnatRestReadDeserializationModule
import org.nrg.xnat.versions.XnatVersion

import static org.nrg.xnat.interfaces.XnatInterface.XNAT_REST_MAPPER

class XnatRestReadObjectMapper extends ObjectMapper {

    @Deprecated
    XnatRestReadObjectMapper(boolean strict = true) {
        super()
        if (!strict) {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        }
        registerModule(new XnatRestReadDeserializationModule())
    }

    XnatRestReadObjectMapper(Class<? extends XnatVersion> versionClass, boolean strict = true) {
        super()
        if (!strict) {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        }
        registerModule(new XnatRestReadDeserializationModule(versionClass))
    }

    def <X> List<X> deserializeList(List serialized, Class<X> deserializedObjectClass) {
        serialized.collect { object ->
            deserializeObject(object, deserializedObjectClass)
        }
    }

    def <X> X deserializeObject(Object object, Class<X> deserializedObjectClass) {
        convertValue(object, deserializedObjectClass)
    }

}
