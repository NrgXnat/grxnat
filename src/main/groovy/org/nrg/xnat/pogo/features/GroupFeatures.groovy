package org.nrg.xnat.pogo.features

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy)
class GroupFeatures {

    List<String> features
    List<String> inheritedFeatures
    List<String> inheritedBanned
    List<String> blocked
    String display
    String id

}
