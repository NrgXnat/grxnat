package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import org.nrg.xnat.jackson.modules.XnatRestWriteSerializationModule

class XnatRestWriteObjectMapper extends ObjectMapper {

    XnatRestWriteObjectMapper() {
        super()
        registerModule(new XnatRestWriteSerializationModule())
    }

}
