package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.resources.ResourceFile

class ResourceFileDeserializer extends CustomDeserializer<ResourceFile> {

    @Override
    ResourceFile deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final ResourceFile resourceFile = new ResourceFile()

        setStringIfNonempty(node, 'Name', resourceFile.&setName)
        setStringIfNonempty(node, 'file_content', resourceFile.&setContent)
        setStringIfNonempty(node, 'file_format', resourceFile.&setFormat)
        setStringIfNonempty(node, 'URI', resourceFile.&setUri)

        resourceFile
    }

}