package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.enums.ContainerType

class Image {

    String user
    String name
    String version = 'latest'
    @JsonProperty('image-id') String imageId
    List<Command> commands = []
    ContainerType type = ContainerType.DOCKER

    Image() {}

    Image(String user, String name, String version) {
        setUser(user)
        setName(name)
        setVersion(version)
    }

    void setNames(List<String> names) {
        if (names != null && !names.isEmpty()) {
            final List<String> slashDivided = names[0].split('/')
            setUser(slashDivided[0])
            final List<String> colonDivided = slashDivided[1].split(':')
            setName(colonDivided[0])
            setVersion(colonDivided[1])
        }
    }

    @Override
    String toString() {
        "${user}/${name}:${version}"
    }

}
