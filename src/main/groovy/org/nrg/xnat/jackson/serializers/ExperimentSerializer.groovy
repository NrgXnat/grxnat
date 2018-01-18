package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.util.TimeUtils

class ExperimentSerializer extends CustomSerializer<Experiment> {

    @Override
    void serialize(Experiment value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonnull(gen, 'xsiType', value.dataType.xsiType)
        if (value.date != null) writeStringFieldIfNonnull(gen, 'date', value.date.format(TimeUtils.MM_DD_YYYY))
        writeStringFieldIfNonnull(gen, 'note', value.notes)
        if (!value.specificFields.isEmpty()) {
            value.specificFields.each { field, fieldVal ->
                gen.writeStringField("${value.dataType.xsiType}/${field}", fieldVal)
            }
        }

        gen.writeEndObject()
    }

}
