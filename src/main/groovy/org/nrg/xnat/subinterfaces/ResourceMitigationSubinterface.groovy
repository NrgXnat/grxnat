package org.nrg.xnat.subinterfaces

import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import org.nrg.testing.CommonStringUtils
import org.nrg.testing.TimeUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.resources.ResourceMitigationReport
import org.nrg.xnat.pogo.resources.ResourceSurveyRequest
import org.nrg.xnat.pogo.resources.SurveyReportSummary

import java.time.LocalDate

class ResourceMitigationSubinterface extends CoreXnatFunctionalitySubinterface {

    protected static final List<String> URL_STATUS = ['status']
    protected static final List<String> URL_REPORT = ['report']
    protected static final TypeRef<Map<String, ResourceSurveyRequest.Status>> STATUS_MAP = new TypeRef<Map<String, ResourceSurveyRequest.Status>>() {}

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/resources/mitigate/project/{projectId}',
                '/xapi/resources/mitigate/project/{projectId}/report',
                '/xapi/resources/mitigate/request/{requestId}',
                '/xapi/resources/mitigate/request/{requestId}/report',
                '/xapi/resources/mitigate/resource/{resourceId}',
                '/xapi/resources/mitigate/resource/{resourceId}/report',
                '/xapi/resources/mitigate/resources',
                '/xapi/resources/survey/project/{projectId}',
                '/xapi/resources/survey/project/{projectId}',
                '/xapi/resources/survey/project/{projectId}',
                '/xapi/resources/survey/project/{projectId}/{status}',
                '/xapi/resources/survey/project/{projectId}/status',
                '/xapi/resources/survey/report/download',
                '/xapi/resources/survey/report/summary',
                '/xapi/resources/survey/request',
                '/xapi/resources/survey/request/{requestId}',
                '/xapi/resources/survey/request/{requestId}',
                '/xapi/resources/survey/request/{requestId}/report',
                '/xapi/resources/survey/resource',
                '/xapi/resources/survey/resource/{resourceId}',
                '/xapi/resources/survey/resource/{resourceId}',
                '/xapi/resources/survey/resource/{resourceId}',
                '/xapi/resources/survey/resource/{resourceId}/report',
                '/xapi/resources/survey/resource/{resourceId}/status',
                '/xapi/resources/survey/resource/status'
        ]
    }

    List<Integer> launchSurveyRequests(Project project, LocalDate since = null) {
        final Map<String, String> queryParams = (since) ? ['since': TimeUtils.UNAMBIGUOUS_DATE.format(since)] : [:]
        queryBaseWithCommonStatusCodeListeners().queryParams(queryParams).post(projectSurveyUrl(project))
                .then().assertThat().statusCode(200).and().extract().as(List<Integer>)
    }

    int launchSurveyRequest(int resourceId) {
        queryBaseWithCommonStatusCodeListeners().post(resourceIdSurveyUrl(resourceId))
                .then().assertThat().statusCode(200).and().extract().asString().toInteger()
    }

    List<ResourceSurveyRequest> readOpenResourceSurveysForProject(Project project) {
        queryBaseWithCommonStatusCodeListeners().get(projectSurveyUrl(project))
                .then().assertThat().statusCode(200).and().extract().as(ResourceSurveyRequest[])
    }

    List<ResourceSurveyRequest> readResourceSurveysForProject(Project project, ResourceSurveyRequest.Status status = null) {
        queryBaseWithCommonStatusCodeListeners().get(projectSurveyUrl(project, [status?.serialize() ?: 'all']))
                .then().assertThat().statusCode(200).and().extract().as(ResourceSurveyRequest[])
    }

    Map<String, ResourceSurveyRequest.Status> readResourceSurveyStatusesForProject(Project project) {
        queryBaseWithCommonStatusCodeListeners().get(projectSurveyUrl(project, URL_STATUS))
                .then().assertThat().statusCode(200).and().extract().as(STATUS_MAP)
    }

    Map<String, ResourceSurveyRequest.Status> readResourceSurveyStatusesByResourceIds(List<Integer> resourceIds) {
        queryBaseWithCommonStatusCodeListeners().contentType(ContentType.JSON).body(resourceIds).post(extensibleSurveyUrl(['resource']))
                .then().assertThat().statusCode(200).and().extract().as(STATUS_MAP)
    }

    ResourceSurveyRequest readResourceSurveyRequestById(int requestId) {
        queryBaseWithCommonStatusCodeListeners().get(requestIdSurveyUrl(requestId))
                .then().assertThat().statusCode(200).and().extract().as(ResourceSurveyRequest)
    }

    ResourceSurveyRequest readResourceSurveyRequestByResourceId(int resourceId) {
        queryBaseWithCommonStatusCodeListeners().get(resourceIdSurveyUrl(resourceId))
                .then().assertThat().statusCode(200).and().extract().as(ResourceSurveyRequest)
    }

    ResourceSurveyRequest.Status readResourceSurveyRequestStatusByResourceId(int resourceId) {
        queryBaseWithCommonStatusCodeListeners().get(resourceIdSurveyUrl(resourceId, URL_STATUS))
                .then().assertThat().statusCode(200).and().extract().as(ResourceSurveyRequest.Status)
    }

    XnatInterface cleanResourceReportsByRequestId(int requestId) {
        queryBaseWithCommonStatusCodeListeners().delete(requestIdSurveyUrl(requestId, URL_REPORT))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface cleanResourceReportsByResourceId(int requestId) {
        queryBaseWithCommonStatusCodeListeners().delete(resourceIdSurveyUrl(requestId, URL_REPORT))
                .then().assertThat().statusCode(200)
        xnatInterface
    }

    SurveyReportSummary readResourceSurveyReportSummaryForProject(Project project) {
        readReportSummary(project).jsonPath().getObject('get(0)', SurveyReportSummary)
    }

    List<SurveyReportSummary> readResourceSurveyReportSummariesForSite() {
        readReportSummary().as(SurveyReportSummary[])
    }

    String readResourceSurveyReportCsvForSite() {
        readSurveyReportCsv()
    }

    String readResourceSurveyReportCsvForProject(Project project) {
        readSurveyReportCsv(project)
    }

    List<Integer> cancelAllOpenResourceSurveyRequestsInProject(Project project) {
        queryBaseWithCommonStatusCodeListeners().delete(projectSurveyUrl(project))
                .then().assertThat().statusCode(200).and().extract().as(List<Integer>)
    }

    List<Integer> cancelResourceSurveyRequestsByRequestIds(List<Integer> requestIds) {
        queryBaseWithCommonStatusCodeListeners().contentType(ContentType.JSON).body(requestIds).delete(extensibleSurveyUrl(['request']))
                .then().assertThat().statusCode(200).and().extract().as(List<Integer>)
    }

    int cancelResourceSurveyRequestByRequestId(int requestId) {
        queryBaseWithCommonStatusCodeListeners().delete(requestIdSurveyUrl(requestId))
                .then().assertThat().statusCode(200).and().extract().as(Integer)
    }

    List<Integer> cancelResourceSurveyRequestsByResourceIds(List<Integer> resourceIds) {
        queryBaseWithCommonStatusCodeListeners().contentType(ContentType.JSON).body(resourceIds).delete(extensibleSurveyUrl(['resource']))
                .then().assertThat().statusCode(200).and().extract().as(List<Integer>)
    }

    int cancelResourceSurveyRequestByResourceId(int resourceId) {
        queryBaseWithCommonStatusCodeListeners().delete(resourceIdSurveyUrl(resourceId))
                .then().assertThat().statusCode(200).and().extract().as(Integer)
    }

    List<Integer> launchMitigationRequestsForProject(Project project, String reason = null, String comment = null) {
        queryBaseWithCommonStatusCodeListeners().queryParams(mitigationParams(reason, comment)).post(mitigateUrl(['project', project.id]))
                .then().assertThat().statusCode(200).and().extract().as(List<Integer>)
    }

    int launchMitigationByResourceId(int resourceId, String reason = null, String comment = null) {
        queryBaseWithCommonStatusCodeListeners().queryParams(mitigationParams(reason, comment)).post(mitigateUrl(['resource', String.valueOf(resourceId)]))
                .then().assertThat().statusCode(200).and().extract().as(Integer)
    }

    ResourceMitigationReport readMitigationReportByResourceId(int resourceId) {
        queryBaseWithCommonStatusCodeListeners().get(mitigateUrl(['resource', String.valueOf(resourceId), 'report']))
                .then().assertThat().statusCode(200).and().extract().as(ResourceMitigationReport)
    }

    List<ResourceMitigationReport> readMitigationReportsByProject(Project project) {
        queryBaseWithCommonStatusCodeListeners().get(mitigateUrl(['project', project.id, 'report']))
                .then().assertThat().statusCode(200).and().extract().as(ResourceMitigationReport[])
    }

    int launchMitigationByRequestId(int requestId, String reason = null, String comment = null) {
        queryBaseWithCommonStatusCodeListeners().queryParams(mitigationParams(reason, comment)).post(mitigateUrl(['request', String.valueOf(requestId)]))
                .then().assertThat().statusCode(200).and().extract().as(Integer)
    }

    ResourceMitigationReport readMitigationReportByRequestId(int requestId) {
        queryBaseWithCommonStatusCodeListeners().get(mitigateUrl(['request', String.valueOf(requestId), 'report']))
                .then().assertThat().statusCode(200).and().extract().as(ResourceMitigationReport)
    }

    Map<String, List<Integer>> launchMitigationViaCsv(File csv) {
        queryBaseWithCommonStatusCodeListeners().multiPart('uploadFile', csv).post(mitigateUrl(['resources', 'csv']))
                .then().assertThat().statusCode(200).and().extract().as(Map)
    }

    protected String resourcesEndpoint(List<String> additionalComponents) {
        formatXapiUrl('resources', CommonStringUtils.formatUrl(additionalComponents.toArray()))
    }

    protected String extensibleResourcesEndpoint(List<String> fixedElements, List<String> passthroughElements) {
        final List<String> components = new ArrayList<>(fixedElements)
        if (passthroughElements) {
            components.addAll(passthroughElements)
        }
        resourcesEndpoint(components)
    }

    protected String extensibleSurveyUrl(List<String> fixedElements, List<String> passthroughElements = null) {
        final List<String> passedFixedElements = ['survey']
        passedFixedElements.addAll(fixedElements)
        extensibleResourcesEndpoint(passedFixedElements, passthroughElements)
    }

    protected String projectSurveyUrl(Project project, List<String> additionalComponents = null) {
        extensibleSurveyUrl(['project', project.id], additionalComponents)
    }

    protected String requestIdSurveyUrl(int requestId, List<String> additionalComponents = null) {
        extensibleSurveyUrl(['request', String.valueOf(requestId)], additionalComponents)
    }

    protected String resourceIdSurveyUrl(int resourceId, List<String> additionalComponents = null) {
        extensibleSurveyUrl(['resource', String.valueOf(resourceId)], additionalComponents)
    }

    protected String mitigateUrl(List<String> elements) {
        extensibleResourcesEndpoint(['mitigate'], elements)
    }

    protected ExtractableResponse readReportSummary(Project project = null) {
        queryBaseWithCommonStatusCodeListeners().queryParams(projectToQueryParam(project)).get(extensibleSurveyUrl(['report', 'summary']))
                .then().assertThat().statusCode(200).and().extract()
    }

    protected String readSurveyReportCsv(Project project = null) {
        queryBaseWithCommonStatusCodeListeners().queryParams(projectToQueryParam(project)).get(extensibleSurveyUrl(['report', 'download']))
                .then().assertThat().statusCode(200).and().extract().asString()
    }

    protected Map<String, String> projectToQueryParam(Project project) {
        (project) ? ['projectId': project.id] : [:]
    }

    protected Map<String, String> mitigationParams(String reason, String comment) {
        final Map<String, String> params = [:]
        if (reason) {
            params.put('reason', reason)
        }
        if (comment) {
            params.put('comment', comment)
        }
        params
    }

}
