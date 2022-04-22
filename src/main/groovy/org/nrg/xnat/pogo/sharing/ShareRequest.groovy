package org.nrg.xnat.pogo.sharing

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.nrg.xnat.enums.ShareMethod

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy)
class ShareRequest {

    ShareMethod operation
    String id
    String destinationProject

}
