package org.nrg.xnat.pogo.resources

class ResourceSurveyRequest {

    enum Status {
        CREATED,
        QUEUED_FOR_SURVEY,
        SURVEYING,
        DIVERGENT,
        CONFORMING,
        QUEUED_FOR_MITIGATION,
        MITIGATING,
        CANCELED,
        ERROR
    }

    int resourceId
    Status rsnStatus = Status.CREATED
    String projectId
    String subjectId
    String experimentId
    String xsiType
    int scanId
    String resourceLabel
    String resourceUri
    String subjectLabel
    String experimentLabel
    String scanLabel
    String scanDescription
    int workflowId
    String requester
    String requestTime
    Date closingDate
    ResourceSurveyReport surveyReport
    ResourceMitigationReport mitigationReport
    Boolean open
    int id

}
