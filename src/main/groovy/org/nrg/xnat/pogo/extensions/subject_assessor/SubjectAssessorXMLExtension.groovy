package org.nrg.xnat.pogo.extensions.subject_assessor

import com.jayway.restassured.http.ContentType
import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class SubjectAssessorXMLExtension extends SubjectAssessorExtension {

    File assessorXML
    XnatInterface xnatInterface

    SubjectAssessorXMLExtension(XnatInterface xnatInterface, SubjectAssessor assessor, File assessorXML) {
        super(assessor)
        this.assessorXML = assessorXML
        this.xnatInterface = xnatInterface
    }

    SubjectAssessorXMLExtension(XnatInterface xnatInterface, File assessorXML) {
        this(xnatInterface, null, assessorXML)
    }

    @Override
    void create(Project project, Subject subject) {
        getParentObject().accessionNumber(
                xnatInterface.queryBase().contentType(ContentType.XML).queryParam('format', 'xml').body(assessorXML.text).
                        post(xnatInterface.formatRestUrl("/projects/${project.id}/subjects/${subject.label}/experiments")).
                        then().assertThat().statusCode(200).and().extract().response().asString().split('/').last()
        )
        final SubjectAssessor createdAssessor = xnatInterface.readExperiment(parentObject.accessionNumber, parentObject.class) as SubjectAssessor
        parentObject.label(createdAssessor.label)
    }

}
