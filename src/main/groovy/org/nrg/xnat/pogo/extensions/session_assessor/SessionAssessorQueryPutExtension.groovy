package org.nrg.xnat.pogo.extensions.session_assessor

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.rest.SerializationUtils

class SessionAssessorQueryPutExtension extends SessionAssessorExtension {

    SessionAssessorQueryPutExtension(SessionAssessor sessionAssessor) {
        super(sessionAssessor)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject, ImagingSession session) {
        if (parentObject.getDataType() == null) {
            throw new UnsupportedOperationException("SessionAssessor must have xsiType to pass to XNAT for this method")
        }

        parentObject.accessionNumber(
                xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).put(url(xnatInterface, project, subject, session, parentObject as SessionAssessor)).
                then().assertThat().statusCode(Matchers.isOneOf(200, 201)).and().extract().response().asString().trim()
        )
    }

    String url(XnatInterface xnatInterface, Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        xnatInterface.sessionAssessorUrl(project, subject, session, sessionAssessor)
    }

}
