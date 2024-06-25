package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.dicomweb.DicomAttribute
import org.nrg.xnat.pogo.dicomweb.DicomModelObject

class JsonDicomModelObjectDeserializer extends CustomDeserializer<DicomModelObject> {

    public static final String TAG_INJECT_KEY = 'tag'

    @Override
    DicomModelObject deserialize(ObjectCodec objectCodec, JsonNode node) throws IOException, JsonProcessingException {
        final DicomModelObject dicomObject = new DicomModelObject()

        dicomObject.attributes = readMapObjectList(node, objectCodec, DicomAttribute, TAG_INJECT_KEY)

        dicomObject
    }

}
