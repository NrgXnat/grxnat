package org.nrg.xnat.pogo.dqr

abstract class PacsRequest {

    String username
    Long pacsId
    String xnatProject
    String studyInstanceUid
    List<String> seriesIds
    String remappingScript
    String destinationAeTitle
    String status
    Long priority
    Date queuedTime
    String studyDate
    String studyId
    String accessionNumber
    String patientId
    String patientName
    long id
    String decodedAeAndPort

}
