package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class DqrSeriesRepresentation implements DqrObject {

    String seriesInstanceUid
    DqrStudyRepresentation study // just for the UID reference
    Integer seriesNumber
    String modality
    String seriesDescription
    String studyDate
    String accessionNumber
    String patientId
    String patientName
    String uniqueIdentifier

}
