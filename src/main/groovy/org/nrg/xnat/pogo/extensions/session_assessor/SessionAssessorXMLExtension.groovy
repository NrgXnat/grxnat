package org.nrg.xnat.pogo.extensions.session_assessor

import com.jayway.restassured.http.ContentType
import org.nrg.testing.CommonUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.util.FileIOUtils

class SessionAssessorXMLExtension extends SessionAssessorExtension {

    XnatInterface xnatInterface
    File assessorXML

    SessionAssessorXMLExtension(XnatInterface xnatInterface, SessionAssessor assessor, File assessorXML) {
        super(assessor)
        this.xnatInterface = xnatInterface
        this.assessorXML = assessorXML
    }

    SessionAssessorXMLExtension(XnatInterface xnatInterface, File assessorXML) {
        this(xnatInterface, null, assessorXML)
    }

    @Override
    void create(Project project, Subject subject, ImagingSession session) {
        parentObject.accessionNumber(
                CommonUtils.last(
                        xnatInterface.queryBase().contentType(ContentType.XML).queryParam('format', 'xml').body(FileIOUtils.readFile(assessorXML)).
                                post(xnatInterface.assessorsUrl(project, subject, session)).then().
                                assertThat().statusCode(200).and().extract().response().asString().split('/')
                )
        )
        final SessionAssessor createdAssessor = xnatInterface.readExperiment(parentObject.accessionNumber, SessionAssessor) as SessionAssessor
        parentObject.label(createdAssessor.getLabel())
    }

}
