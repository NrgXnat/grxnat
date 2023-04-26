package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
class DockerServer {

    Integer id
    String name
    String host
    String certPath
    boolean swarmMode
    Backend backend = null
    String pathTranslationXnatPrefix
    String pathTranslationDockerPrefix
    boolean pullImagesOnXnatInit
    String containerUser
    boolean autoCleanup
    List<SwarmConstraint> swarmConstraints
    boolean ping

    void setSwarmMode(final boolean swarmMode) {
        this.swarmMode = swarmMode
        backend = swarmMode ? Backend.SWARM : (backend == null ? Backend.DOCKER : backend)
    }

    void setBackend(final Backend backend) {
        this.backend = backend
        this.swarmMode = backend == Backend.SWARM
    }

}
