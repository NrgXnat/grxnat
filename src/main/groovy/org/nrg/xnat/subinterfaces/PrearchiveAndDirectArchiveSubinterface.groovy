package org.nrg.xnat.subinterfaces

import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.pogo.paginated_api.HibernateFilter
import org.nrg.xnat.pogo.paginated_api.PaginatedRequest
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.dicom.SessionData
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

import static com.jayway.restassured.http.ContentType.JSON

class PrearchiveAndDirectArchiveSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/prearchive/projects/{PROJECT_ID}',
                '/prearchive/projects/{PROJECT_ID}/{SESSION_TIMESTAMP}/{SESSION_LABEL}/logs',
                '/xapi/direct-archive'
        ]
    }
    
    int getPrearchiveEntryCountForProject(Project project) {
        jsonQuery().get(projectPrearchiveUrl(project)).jsonPath().
                getInt('ResultSet.Result.size()')
    }

    List<SessionData> getPrearchiveEntriesForProjectWithSessionLabel(Project project, ImagingSession session) {
        deserialize(
                jsonQuery().get(projectPrearchiveUrl(project)).
                        then().assertThat().statusCode(200).and().extract().jsonPath().
                        getList("ResultSet.Result.findAll { it.name == '${session.label}' }")
        )
    }

    List<String> getPrearchiveLogMessages(Project project, SessionData session) {
        jsonQuery().get(CommonStringUtils.formatUrl(projectPrearchiveUrl(project), session.timestamp, session.folderName, 'logs')).
                then().assertThat().statusCode(200).and().extract().jsonPath().
                getList('ResultSet.Result.entry', String)
    }

    List<SessionData> getDirectArchiveEntriesForProject(Project project) {
        queryDirectArchiveEntries(new PaginatedRequest().filter('project',
                new HibernateFilter().operator(HibernateFilter.Operator.EQ).value(project.id)))
    }

    List<SessionData> getDirectArchiveEntries() {
        queryDirectArchiveEntries(new PaginatedRequest())
    }
    
    String projectPrearchiveUrl(Project project) {
        formatRestUrl('prearchive', 'projects', project.id)
    }

    protected List<SessionData> deserialize(List response) {
        SerializationUtils.deserializeList(response, SessionData)
    }

    protected List<SessionData> queryDirectArchiveEntries(PaginatedRequest requestSpec) {
        deserialize(
                queryBase().contentType(JSON).body(requestSpec).
                        post(formatXapiUrl('direct-archive')).
                        then().assertThat().statusCode(200).extract().as(List)
        )
    }

}
