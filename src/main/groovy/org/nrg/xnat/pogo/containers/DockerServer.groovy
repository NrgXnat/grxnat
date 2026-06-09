package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy)
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
    List<KubernetesToleration> kubernetesTolerations
    boolean ping
    String combinedPvcName
    String combinedPathTranslation
    String archivePvcName
    String archivePathTranslation
    String buildPvcName
    String buildPathTranslation

    void setSwarmMode(final boolean swarmMode) {
        this.swarmMode = swarmMode
        backend = swarmMode ? Backend.SWARM : (backend == null ? Backend.DOCKER : backend)
    }

    void setBackend(final Backend backend) {
        this.backend = backend
        this.swarmMode = backend == Backend.SWARM
    }

}
