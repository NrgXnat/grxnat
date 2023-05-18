package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode

class StringIntBooleanDeserializer extends CustomDeserializer<Boolean> {

    @Override
    Boolean deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final String asText = node.asText()
        switch (asText) {
            case '0':
                return false
            case '1':
                return true
            default:
                throw new IOException("Unknown value: ${asText}")
        }
    }

}
