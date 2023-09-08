package org.nrg.xnat.pogo.dqr

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@JsonInclude(JsonInclude.Include.NON_NULL)
class PacsSearchCriteria {

    long pacsId
    String patientId
    String patientName
    String dob
    String accessionNumber
    String studyInstanceUid
    String studyId
    String seriesInstanceUid
    String seriesDescription
    Integer seriesNumber
    String modality
    DqrDateRange studyDateRange

    static PacsSearchCriteria forPacs(PacsConnection pacs) {
        new PacsSearchCriteria(pacsId: pacs.id)
    }

}
