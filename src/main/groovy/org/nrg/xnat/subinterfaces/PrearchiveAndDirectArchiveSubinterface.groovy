package org.nrg.xnat.subinterfaces

import org.nrg.xnat.pogo.HibernateFilter
import org.nrg.xnat.pogo.PaginatedRequest
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.QueryFilter
import org.nrg.xnat.pogo.dicom.SessionData
import org.nrg.xnat.pogo.experiments.ImagingSession

import static com.jayway.restassured.http.ContentType.JSON

class PrearchiveAndDirectArchiveSubinterface extends XnatFunctionalitySubinterface {
    @Override
    List<String> getHandledEndpoints() {
        [
                '/data/prearchive/projects/{project}',
                '/data/prearchive/projects/{project}/{timestamp}/{session}/logs'
        ]
    }
    
    int getPrearchiveEntryCountForProject(Project project) {
        jsonQuery().get(formatRestUrl("prearchive", "projects", project.id)).jsonPath().
                getInt('ResultSet.Result.size()')
    }

    SessionData[] getPrearchiveEntriesForProjectWithSessionLabel(Project project, ImagingSession session) {
        jsonQuery().get(formatRestUrl("prearchive", "projects", project.id)).
                then().assertThat().statusCode(200).and().extract().jsonPath()
                .getObject("ResultSet.Result.findAll { it.name == '${session.label}' }", SessionData[])
    }

    List<String> getPrearchiveLogMessages(Project project, SessionData session) {
        jsonQuery().get(formatRestUrl("prearchive", "projects", project.id, session.timestamp,
                    session.folderName, "logs")).
                then().assertThat().statusCode(200).and().extract().jsonPath().
                getList("ResultSet.Result.collect { it.entry }", String.class);
    }

    SessionData[] getDirectArchiveEntriesForProject(Project project) {
        PaginatedRequest paginatedRequest = new PaginatedRequest().filter("project",
                new HibernateFilter().operator(HibernateFilter.Operator.EQ).value(project.id))
        queryBase().contentType(JSON).body(paginatedRequest).
                post(formatXapiUrl("direct-archive")).
                then().assertThat().statusCode(200).extract().as(SessionData[].class)
    }

    SessionData[] getDirectArchiveEntries() {
        queryBase().contentType(JSON).body(new PaginatedRequest()).
                post(formatXapiUrl("direct-archive")).
                then().assertThat().statusCode(200).extract().as(SessionData[].class)
    }
}
