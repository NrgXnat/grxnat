package org.nrg.xnat.pogo.search

import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.nrg.xnat.jackson.naming.AllCapsSnakeCaseStrategy

@JsonNaming(AllCapsSnakeCaseStrategy)
class DisplayField {

    String summary
    String src
    String fieldId
    String header
    String requiresValue
    String elementName
    String type
    String desc

}
