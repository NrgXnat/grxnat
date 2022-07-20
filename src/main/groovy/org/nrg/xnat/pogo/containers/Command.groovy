package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.util.ListUtils

class Command {

    String name
    int id
    @JsonProperty('xnat') final private List<Wrapper> wrappers = []

    List<Wrapper> getWrappers() {
        wrappers
    }

    void setWrappers(List<Wrapper> wrappers) {
        ListUtils.copyInto(wrappers, this.wrappers)
    }

    Command name(String name) {
        setName(name)
        this
    }

    Command id(int id) {
        setId(id)
        this
    }

    Command wrappers(List<Wrapper> wrappers) {
        ListUtils.copyInto(wrappers, this.wrappers)
        this
    }

}
