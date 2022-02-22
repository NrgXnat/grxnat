package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.jackson.XnatSerializationProvider
import org.nrg.xnat.pogo.Investigator
import org.nrg.xnat.pogo.Project

class ProjectSerializer extends CustomSerializer<Project> {

    @Override
    void serialize(Project value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeName(value, gen)
        writeSecondaryId(value, gen)

        writeDescription(value, gen)
        writeKeywords(value, gen)
        writeAccessibility(value, gen)

        if (value.pi != null) {
            int id = value.pi.xnatInvestigatordataId
            if (id > 0) {
                gen.writeNumberField('xnat:projectData/pi_xnat_investigatordata_id', id)
            } else {
                throw new IOException("Project ${value} PI is missing investigator ID. Investigator must exist already in XNAT and have ID registered here to add PI to project.")
            }
        }

        if (!value.investigators.isEmpty()) {
            value.investigators.eachWithIndex { Investigator investigator, int i ->
                int id = investigator.xnatInvestigatordataId
                if (id > 0) {
                    gen.writeNumberField("xnat:projectData/investigators/investigator[${i + 1}]/xnat_investigatordata_id", id)
                } else {
                    throw new IOException("Project investigator ${investigator} is missing investigator ID. Investigator must already exist ")
                }
            }
        }

        if (!value.aliases.isEmpty()) {
            value.aliases.eachWithIndex{ String alias, int i ->
                gen.writeStringField("xnat:projectData/aliases/alias[${i}]/alias", alias)
            }
        }

        gen.writeEndObject()
    }

    protected void writeName(Project project, JsonGenerator gen) {
        writeStringFieldIfNonnull(gen, 'name', project.title)
    }

    protected void writeSecondaryId(Project project, JsonGenerator gen) {
        writeStringFieldIfNonnull(gen, 'secondary_ID', project.runningTitle)
    }

    protected void writeDescription(Project project, JsonGenerator gen) {
        writeStringFieldIfNonnull(gen, 'description', project.description)
    }

    protected void writeKeywords(Project project, JsonGenerator gen) {
        writeListAsSpacedString(gen, 'keywords', project.keywords)
    }

    protected void writeAccessibility(Project project, JsonGenerator gen) {
       writeEnumToString(gen, 'accessibility', project.accessibility)
    }

}
