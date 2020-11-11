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
    String pathTranslationXnatPrefix
    String pathTranslationDockerPrefix
    boolean pullImagesOnXnatInit
    String containerUser
    boolean autoCleanup
    List<SwarmConstraint> swarmConstraints

}
