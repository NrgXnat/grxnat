package org.nrg.xnat.pogo.containers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy)
class CommandSummaryForContext {

    int commandId
    String commandName
    String commandLabel
    String commandDescription
    int wrapperId
    String wrapperName
    String wrapperLabel
    String wrapperDescription
    String imageName
    String imageType
    String rootElementName
    boolean enabled

}
