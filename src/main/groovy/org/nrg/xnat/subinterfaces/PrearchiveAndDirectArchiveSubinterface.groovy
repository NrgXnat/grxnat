package org.nrg.xnat.subinterfaces

import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.paginated_api.HibernateFilter
import org.nrg.xnat.pogo.paginated_api.PaginatedRequest
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.resources.PrearcScanResource
import org.nrg.xnat.prearchive.PrearchiveQuery
import org.nrg.xnat.prearchive.PrearchiveQueryScope
import org.nrg.xnat.prearchive.PrearchiveResultExpectations
import org.nrg.xnat.prearchive.PrearchiveResultFilter
import org.nrg.xnat.prearchive.SessionData
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.rest.SerializationUtils

import java.util.concurrent.TimeUnit

import static io.restassured.http.ContentType.JSON
import static org.awaitility.Awaitility.*

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
        queryPrearchive(
                new PrearchiveQuery()
                        .filter(new PrearchiveResultFilter().name(session.label))
                        .scope(PrearchiveQueryScope.forProject(project))
        )
    }

    List<SessionData> getPrearchiveEntriesForProject(Project project) {
        queryPrearchive(new PrearchiveQuery().scope(PrearchiveQueryScope.forProject(project)))
    }

    List<SessionData> getUnassignedPrearchiveEntries() {
        queryPrearchive(new PrearchiveQuery().scope(PrearchiveQueryScope.UNASSIGNED))
    }

    List<SessionData> queryPrearchive(PrearchiveQuery prearchiveQuery = new PrearchiveQuery()) {
        final PrearchiveResultExpectations resultExpectations = prearchiveQuery.expectedResult
        final PrearchiveResultFilter resultFilter = prearchiveQuery.filter
        await().atMost(prearchiveQuery.maxQueryTimeSeconds, TimeUnit.SECONDS).until(
                {
                    final List<SessionData> results = deserialize(
                            jsonQuery().get(formatRestUrl(prearchiveQuery.scope.getUrlFragments() as String[])).
                                    then().assertThat().statusCode(200).and().extract().jsonPath().
                                    getList('ResultSet.Result')
                    )
                    resultFilter != null ? resultFilter.filter(results) : results
                },
                resultExpectations.matcher
        )
    }

    SessionData queryPrearchiveForSingularResult(PrearchiveQuery prearchiveQuery) {
        queryPrearchive(prearchiveQuery.expectedResult(PrearchiveResultExpectations.SINGLE_RESULT))[0]
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

    XnatInterface rebuildSession(String src, boolean async) {
        if (async) {
            queryBase().formParam('src', src).post(formatRestUrl('/services/prearchive/rebuild')).then().assertThat().statusCode(200)
        } else {
            queryBase().queryParam('action', 'build').post(formatRestUrl(src)).then().assertThat().statusCode(200)
        }
        xnatInterface
    }

    XnatInterface rebuildSession(SessionData sessionData, boolean async) {
        rebuildSession(sessionData.url, async)
    }

    XnatInterface archiveSession(String src) {
        queryBase().formParam('src', src).post(formatRestUrl('/services/archive')).then().assertThat().statusCode(200)
        xnatInterface
    }

    XnatInterface archiveSession(SessionData sessionData) {
        archiveSession(sessionData.url)
    }

    List<Scan> readScansForPrearchiveSession(SessionData sessionData) {
        jsonQuery().get(formatRestUrl(sessionData.url, 'scans')).then().assertThat().statusCode(200).and().extract().jsonPath().getObject('ResultSet.Result', Scan[]).collect { scan ->
            if (xnatInterface.readResources) {
                scan.scanResources(xnatInterface.readResources(new PrearcScanResource(sessionData, scan)))
            }
            scan
        }
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
