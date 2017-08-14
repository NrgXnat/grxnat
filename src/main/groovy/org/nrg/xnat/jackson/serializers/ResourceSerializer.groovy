package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.resources.Resource

class ResourceSerializer extends CustomSerializer<Resource> {

    @Override
    void serialize(Resource value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonnull(gen, 'format', value.format)

        gen.writeEndObject()
    }

}
