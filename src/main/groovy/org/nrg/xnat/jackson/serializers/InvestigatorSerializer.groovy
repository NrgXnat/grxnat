package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.Investigator

class InvestigatorSerializer extends CustomSerializer<Investigator> {

    @Override
    void serialize(Investigator value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonempty(gen, 'title', value.title)
        writeStringFieldIfNonempty(gen, 'firstname', value.firstname)
        writeStringFieldIfNonempty(gen, 'lastname', value.lastname)
        writeStringFieldIfNonempty(gen, 'institution', value.institution)
        writeStringFieldIfNonempty(gen, 'department', value.department)
        writeStringFieldIfNonempty(gen, 'email', value.email)
        writeStringFieldIfNonempty(gen, 'phone', value.phone)

        gen.writeEndObject()
    }
}
