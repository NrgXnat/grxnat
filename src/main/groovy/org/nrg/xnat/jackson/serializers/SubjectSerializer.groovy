package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.Subject

import java.time.format.DateTimeFormatter

class SubjectSerializer extends CustomSerializer<Subject> {

    @Override
    void serialize(Subject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStandardValues(gen, value)
        if (value.getPi() != null) {
            gen.writeStringField('pi_firstname', value.getPi().firstname)
            gen.writeStringField('pi_lastname', value.getPi().lastname)
        }

        gen.writeEndObject()
    }

    protected void writeStandardValues(JsonGenerator gen, Subject subject) {
        writeEnumToString(gen, 'gender', subject.gender)
        writeIntFieldIfNonzero(gen, 'yob', subject.yob)
        writeIntFieldIfNonzero(gen, 'age', subject.age)
        writeEnumToString(gen, 'handedness', subject.handedness)
        writeIntFieldIfNonzero(gen, 'education', subject.education)
        writeStringFieldIfNonempty(gen, 'race', subject.race)
        writeStringFieldIfNonempty(gen, 'ethnicity', subject.ethnicity)
        writeIntFieldIfNonzero(gen, 'height', subject.height)
        writeIntFieldIfNonzero(gen, 'weight', subject.weight)
        writeStringFieldIfNonempty(gen, 'src', subject.src)
        writeStringFieldIfNonempty(gen, 'group', subject.group)
        if (subject.dob != null) {
            gen.writeStringField('dob', subject.dob.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }

}
