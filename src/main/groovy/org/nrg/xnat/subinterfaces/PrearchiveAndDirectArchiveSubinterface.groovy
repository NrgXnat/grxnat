package org.nrg.xnat.subinterfaces

import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.paginated_api.HibernateFilter
import org.nrg.xnat.pogo.paginated_api.PaginatedRequest
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.prearchive.SessionData
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

import static io.restassured.http.ContentType.JSON

class PrearchiveAndDirectArchiveSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/prearchive/projects/{PROJECT_ID}',
                '/xapi/direct-archive'
        ]
    }
    
    int getPrearchiveEntryCountForProject(Project project) {
        getPrearchiveEntriesForProject(project).size()
    }

    List<SessionData> getPrearchiveEntriesForProjectWithSessionLabel(Project project, ImagingSession session) {
        getPrearchiveEntriesForProject(project).findAll { entry ->
            entry.name == session.label
        }
    }

    List<SessionData> getPrearchiveEntriesForProject(Project project) {
        deserialize(
                jsonQuery().get(projectPrearchiveUrl(project)).
                        then().assertThat().statusCode(200).and().extract().jsonPath().
                        getList('ResultSet.Result')
        )
    }

    List<SessionData> getUnassignedPrearchiveEntries() {
        getPrearchiveEntriesForProject(new Project('Unassigned'))
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

    XnatInterface movePrearchiveSession(String src, Project project, Boolean async = true) {
        final Map<String, Object> params = [
                'src' : src,
                'newProject' : project.id
        ]
        if (async != null) {
            params.put('async', async)
        }
        queryBase().queryParams(params).post(formatRestUrl('/services/prearchive/move')).then().assertThat().statusCode(200)
        xnatInterface
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
