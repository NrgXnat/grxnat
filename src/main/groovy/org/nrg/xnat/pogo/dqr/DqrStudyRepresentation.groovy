package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class DqrStudyRepresentation {

    String studyInstanceUid
    String studyDescription
    String accessionNumber
    String studyDate
    List<String> modalitiesInStudy
    DqrPatientRepresentation patient
    String projectId
    String studyId

}
