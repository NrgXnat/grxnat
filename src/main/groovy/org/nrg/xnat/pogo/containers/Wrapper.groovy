package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
class Wrapper {

    String uniqueAlias
    int id
    String name
    List<ExternalInput> externalInputs

    Wrapper uniqueAlias(String alias) {
        setUniqueAlias(alias)
        this
    }

    Wrapper id(int id) {
        setId(id)
        this
    }

    Wrapper name(String name) {
        setName(name)
        this
    }

}
