package org.nrg.xnat.pogo.containers

import org.nrg.xnat.enums.ContainerType

class Image {

    String user
    String name
    String version = 'latest'
    List<Command> commands = []
    ContainerType type = ContainerType.DOCKER

}
