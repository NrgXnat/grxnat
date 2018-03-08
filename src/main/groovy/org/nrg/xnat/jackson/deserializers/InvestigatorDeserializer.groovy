package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.Investigator

class InvestigatorDeserializer extends CustomDeserializer<Investigator> {

    @Override
    Investigator deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final Investigator investigator = new Investigator()

        setStringIfNonnull(node, 'title', investigator.&setTitle)
        setStringIfNonnull(node, 'firstname', investigator.&setFirstname)
        setStringIfNonnull(node, 'lastname', investigator.&setLastname)
        setStringIfNonnull(node, 'xnatInvestigatordataId', investigator.&setId)
        setStringIfNonnull(node, 'email', investigator.&setEmail)
        setStringIfNonnull(node, 'phone', investigator.&setPhone)
        setStringIfNonnull(node, 'department', investigator.&setDepartment)
        setStringIfNonnull(node, 'institution', investigator.&setInstitution)
        setSimpleStringList(node, 'primaryProjects', investigator.&setPrimaryProjects)
        setSimpleStringList(node, 'investigatorProjects', investigator.&setInvestigatorProjects)

        investigator
    }

}
