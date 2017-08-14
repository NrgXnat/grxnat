package org.nrg.xnat.jackson.mappers

import org.nrg.xnat.jackson.modules.XnatRestWriteSerializationModule

class XnatRestReadWriteObjectMapper extends XnatRestReadObjectMapper {

    XnatRestReadWriteObjectMapper() {
        super(false)
        registerModule(XnatRestWriteSerializationModule.build())
    }

}
