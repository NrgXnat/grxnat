package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.Reconstruction

class ReconstructionSerializer extends CustomSerializer<Reconstruction> {

    @Override
    void serialize(Reconstruction value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonnull(gen, 'type', value.type)
        writeStringFieldIfNonnull(gen, 'baseScanType', value.baseScanType)

        gen.writeEndObject()
    }

}
