package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.Project

class ProjectDeserializer extends CustomDeserializer<Project> {

    @Override
    Project deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final Project project = new Project()

        setStringIfNonnull(node, 'ID', project.&setId)
        setStringIfNonnull(node, 'secondary_ID', project.&setRunningTitle)
        setStringIfNonnull(node, 'name', project.&setTitle)
        setStringIfNonnull(node, 'description', project.&setDescription)
        setSpacedStringAsList(node, 'keywords', project.&setKeywords)

        project
    }

}
