package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.apache.commons.lang3.StringUtils
import org.nrg.testing.TimeUtils
import org.nrg.xnat.jackson.XnatSerialize
import org.nrg.xnat.pogo.experiments.Experiment

class ExperimentSerializer extends CustomSerializer<Experiment> {

    @Override
    void serialize(Experiment value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonnull(gen, 'xsiType', value.dataType.xsiType)
        if (value.date != null) writeStringFieldIfNonnull(gen, 'date', value.date.format(TimeUtils.MM_DD_YYYY))
        writeStringFieldIfNonnull(gen, 'note', value.notes)

        final Map<String, Object> additionalFields = [:]
        value.specificFields.each { field, fieldVal ->
            additionalFields.put(field, fieldVal)
        }
        value.class.getDeclaredFields().each { field ->
            if (field.isAnnotationPresent(XnatSerialize)) {
                field.setAccessible(true)
                additionalFields.put(field.name, field.get(value))
            }
        }

        if (!additionalFields.isEmpty()) {
            additionalFields.each { field, fieldVal ->
                gen.writeObjectField(
                        StringUtils.prependIfMissing(field, "${value.dataType.xsiType}/"),
                        fieldVal
                )
            }
        }

        gen.writeEndObject()
    }

}
