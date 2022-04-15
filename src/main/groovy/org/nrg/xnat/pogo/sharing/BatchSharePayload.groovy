package org.nrg.xnat.pogo.sharing

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy)
class BatchSharePayload {

    List<ShareRequest> requests = []
    String trackingId

}
