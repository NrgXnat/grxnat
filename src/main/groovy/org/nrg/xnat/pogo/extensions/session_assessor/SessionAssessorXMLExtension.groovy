package org.nrg.xnat.pogo.extensions.session_assessor

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor

class SessionAssessorXMLExtension extends SessionAssessorExtension {

    File assessorXML

    SessionAssessorXMLExtension(SessionAssessor assessor, File assessorXML) {
        super(assessor)
        this.assessorXML = assessorXML
    }

    SessionAssessorXMLExtension(File assessorXML) {
        this(null, assessorXML)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject, ImagingSession session) {
        parentObject.accessionNumber(
                xnatInterface.queryBase().contentType(ContentType.XML).queryParam('format', 'xml').body(assessorXML.text).
                        post(xnatInterface.assessorsUrl(project, subject, session)).then().
                        assertThat().statusCode(200).and().extract().response().asString().split('/').last()
        )
        final SessionAssessor createdAssessor = xnatInterface.readExperiment(parentObject.accessionNumber, SessionAssessor) as SessionAssessor
        parentObject.label(createdAssessor.getLabel())
    }

}
