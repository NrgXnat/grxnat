package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.databind.SerializationFeature
import org.nrg.xnat.jackson.modules.XnatRestWriteSerializationModule

class XnatRestReadWriteObjectMapper extends XnatRestReadObjectMapper {

    XnatRestReadWriteObjectMapper() {
        super(false)
        registerModule(new XnatRestWriteSerializationModule())
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
    }

}
