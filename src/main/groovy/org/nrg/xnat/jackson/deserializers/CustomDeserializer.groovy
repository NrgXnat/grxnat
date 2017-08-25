package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.lang3.StringUtils
import org.nrg.xnat.pogo.resources.Resource

@SuppressWarnings("GrMethodMayBeStatic")
abstract class CustomDeserializer<T> extends StdDeserializer<T> {

    protected static final TypeReference NESTED_STRING_MAP = new TypeReference<Map<String, Map<String, String>>>() {}

    protected CustomDeserializer(Class<T> aClass) {
        super(aClass)
    }

    protected CustomDeserializer() {
        this(null)
    }

    @Override
    T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return deserialize(p.codec, p.codec.readTree(p) as JsonNode)
    }

    abstract T deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException

    protected void setStringIfNonnull(JsonNode parentNode, String field, Closure method) {
        if (fieldNonnull(parentNode, field)) method(parentNode.get(field).asText())
    }

    protected void setStringIfNonempty(JsonNode parentNode, String field, Closure method) {
        if (fieldNonempty(parentNode, field)) method(parentNode.get(field).asText())
    }

    protected void setIntIfNonzero(JsonNode parentNode, String field, Closure method) {
        if (fieldNonnull(parentNode, field) && parentNode.get(field).asInt() != 0) method(parentNode.get(field).asInt())
    }

    protected void setBoolean(JsonNode parentNode, String field, Closure method) {
        if (parentNode.has(field)) method(parentNode.get(field).asBoolean())
    }

    protected void setSpacedStringAsList(JsonNode parentNode, String field, Closure method) {
        if (fieldNonnull(parentNode, field)) method(parentNode.get(field).asText().split(" ") as List)
    }

    protected void setObject(JsonNode parentNode, String field, ObjectCodec codec, Class objectClass, Closure method) {
        if (fieldNonnull(parentNode, field)) {
            method(readObject(parentNode.get(field), codec, objectClass))
        }
    }

    protected void setObjectList(JsonNode parentNode, String field, ObjectCodec codec, Class objectClass, Closure method) {
        if (fieldNonnull(parentNode, field)) {
            method((codec as ObjectMapper).readerFor(objectClass).readValues(parentNode.get(field).toString()).readAll())
        }
    }

    protected void setSimpleStringList(JsonNode parentNode, String field, Closure method) {
        if (fieldNonnull(parentNode, field)) {
            method(
                    parentNode.get(field).collect { textNode ->
                        textNode.asText()
                    }
            )
        }
    }

    protected void setMapList(JsonNode parentNode, String field, ObjectCodec codec, Class objectClass, String injectKeyAs, Closure setListMethod) {
        if (fieldNonnull(parentNode, field)) {
            setListMethod(parentNode.get(field).fields().collect { entry ->
                final JsonNode objectNode = entry.value
                (objectNode as ObjectNode).put(injectKeyAs, entry.key)
                readObject(objectNode, codec, objectClass)
            })
        }
    }

    protected void setResources(JsonNode parentNode, ObjectCodec codec, Class<? extends Resource> resourceClass, Closure setResourceMethod) {
        setMapList(parentNode, 'resources', codec, resourceClass, 'label', setResourceMethod)
    }

    protected boolean fieldNonnull(JsonNode parentNode, String field) {
        parentNode.has(field) && !parentNode.get(field).isNull()
    }

    protected boolean fieldNonempty(JsonNode parentNode, String field) {
        fieldNonnull(parentNode, field) && !StringUtils.isEmpty(parentNode.get(field).asText())
    }

    protected <X> X readObject(JsonNode node, ObjectCodec codec, Class<X> objectClass) {
        (codec as ObjectMapper).readValue(node.toString(), objectClass)
    }

    protected <X> List<X> readMapObjectList(JsonNode node, ObjectCodec codec, Class<X> objectClass, String injectKeyAs) {
        node.fields().collect { entry ->
            final JsonNode objectNode = entry.value
            (objectNode as ObjectNode).put(injectKeyAs, entry.key)
            readObject(objectNode, codec, objectClass)
        }
    }

}
