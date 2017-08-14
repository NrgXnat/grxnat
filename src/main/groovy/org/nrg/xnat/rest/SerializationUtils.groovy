package org.nrg.xnat.rest

import com.fasterxml.jackson.core.type.TypeReference
import org.nrg.xnat.interfaces.XnatInterface

class SerializationUtils {

    private static final TypeReference<Map<String, Object>> mapTypeRef = new TypeReference<Map<String, Object>>(){}

    static Map<String, Object> serializeToMap(Object object) {
        try {
            return XnatInterface.XNAT_REST_MAPPER.readValue(XnatInterface.XNAT_REST_MAPPER.writeValueAsString(object), mapTypeRef)
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in serializing object.", e)
        }
    }

}
