package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.Subject

class SubjectDeserializer extends CustomDeserializer<Subject> {

    @Override
    Subject deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final Subject subject = new Subject(null)

        setStringIfNonnull(node, 'label', subject.&setLabel)
        setStringIfNonnull(node, 'gender', subject.&setGender)
        setIntIfNonzero(node, 'yob', subject.&setYob)
        setIntIfNonzero(node, 'age', subject.&setAge)
        setStringIfNonnull(node, 'handedness', subject.&setHandedness)
        setIntIfNonzero(node, 'height', subject.&setHeight)
        setIntIfNonzero(node, 'weight', subject.&setWeight)
        setStringIfNonnull(node, 'src', subject.&setSrc)
        setIntIfNonzero(node, 'education', subject.&setEducation)
        setStringIfNonnull(node, 'race', subject.&setRace)
        setStringIfNonnull(node, 'ethnicity', subject.&setEthnicity)
        setCustomVariables(node, codec, subject)

        subject
    }

}