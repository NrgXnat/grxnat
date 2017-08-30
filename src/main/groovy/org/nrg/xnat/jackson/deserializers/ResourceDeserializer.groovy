package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.resources.Resource

class ResourceDeserializer<T extends Resource> extends CustomDeserializer<Resource> {

    ResourceDeserializer(Class<? extends Resource> resourceClass) {
        super(resourceClass)
    }

    @Override
    T deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final T resource = handledType().newInstance() as T

        setStringIfNonnull(node, 'label', resource.&setFolder)
        setStringIfNonnull(node, 'format', resource.&setFormat)
        if (fieldNonnull(node, 'file_count')) resource.setFileCount(node.get('file_count').asInt())
        if (fieldNonnull(node, 'file_size')) resource.setFileSize(node.get('file_size').asLong())

        resource
    }

}

