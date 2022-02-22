package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.databind.SerializationFeature
import org.nrg.xnat.jackson.modules.XnatRestWriteSerializationModule
import org.nrg.xnat.rest.SerializationUtils
import org.nrg.xnat.versions.XnatVersion

class XnatRestReadWriteObjectMapper extends XnatRestReadObjectMapper {

    @Deprecated
    XnatRestReadWriteObjectMapper() {
        super(false)
        registerModule(new XnatRestWriteSerializationModule())
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
    }

    XnatRestReadWriteObjectMapper(Class<? extends XnatVersion> versionClass) {
        super(versionClass, false)
        registerModule(new XnatRestWriteSerializationModule(versionClass))
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
    }

    Map<String, Object> serializeToMap(Object object) {
        try {
            readValue(writeValueAsString(object), SerializationUtils.MAP_TYPE_REF)
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in serializing object.", e)
        }
    }

}
