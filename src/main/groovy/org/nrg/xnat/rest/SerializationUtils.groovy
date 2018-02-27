package org.nrg.xnat.rest

import com.fasterxml.jackson.core.type.TypeReference

import static org.nrg.xnat.interfaces.XnatInterface.XNAT_REST_MAPPER

class SerializationUtils {

    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>(){}
    public static final TypeReference<Map<String, String>> STRING_STRING_MAP = new TypeReference<Map<String, String>>(){}

    static Map<String, Object> serializeToMap(Object object) {
        try {
            XNAT_REST_MAPPER.readValue(XNAT_REST_MAPPER.writeValueAsString(object), MAP_TYPE_REF)
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in serializing object.", e)
        }
    }

    static <X> List<X> deserializeList(List serialized, Class<X> deserializedObjectClass) {
        serialized.collect { object ->
            deserializeObject(object, deserializedObjectClass)
        }
    }

    static <X> X deserializeObject(Object object, Class<X> deserializedObjectClass) {
        XNAT_REST_MAPPER.convertValue(object, deserializedObjectClass)
    }

}
