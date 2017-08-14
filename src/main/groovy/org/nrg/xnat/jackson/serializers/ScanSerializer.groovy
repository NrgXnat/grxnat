package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.experiments.Scan

class ScanSerializer extends CustomSerializer<Scan> {

    @Override
    void serialize(Scan value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonnull(gen, 'series_description', value.seriesDescription)
        writeCommonFields(gen, value)

        gen.writeEndObject()
    }
    
    protected void writeCommonFields(JsonGenerator gen, Scan scan) {
        writeStringFieldIfNonnull(gen, 'xsiType', scan.xsiType)
        writeStringFieldIfNonnull(gen, 'type', scan.type)
        writeStringFieldIfNonnull(gen, 'note', scan.note)
        writeStringFieldIfNonnull(gen, 'quality', scan.quality)
    }

}
