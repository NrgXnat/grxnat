package org.nrg.xnat.pogo.containers

class Wrapper {

    String uniqueAlias
    int id
    String name

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
