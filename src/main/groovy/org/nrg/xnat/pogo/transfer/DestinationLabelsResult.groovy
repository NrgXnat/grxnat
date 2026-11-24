package org.nrg.xnat.pogo.transfer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * {@code POST /xapi/transfer/validate/destination_labels}: the destination's project ID and, of the labels asked
 * about, those a session there already has (its own or shared in), sorted.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown = true)
class DestinationLabelsResult {

    String destinationProject
    List<String> existingSessionLabels = []

}
