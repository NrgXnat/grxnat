package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.resources.ResourceFile

class ResourceFileSerializer extends CustomSerializer<ResourceFile> {

    @Override
    void serialize(ResourceFile value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        gen.writeBooleanField('extract', value.unzip)
        if (value.overwrite != null) {
            gen.writeBooleanField('overwrite', value.overwrite)
        }
        writeContentField(value, gen)

        gen.writeEndObject()
    }

    protected void writeContentField(ResourceFile file, JsonGenerator gen) {
        writeStringFieldIfNonnull(gen, 'content', file.content)
    }

}
