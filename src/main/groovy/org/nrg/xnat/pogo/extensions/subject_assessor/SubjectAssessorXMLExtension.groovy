package org.nrg.xnat.pogo.extensions.subject_assessor

import io.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.SubjectAssessor

class SubjectAssessorXMLExtension extends SubjectAssessorExtension {

    File assessorXML

    SubjectAssessorXMLExtension(SubjectAssessor assessor, File assessorXML) {
        super(assessor)
        this.assessorXML = assessorXML
    }

    SubjectAssessorXMLExtension(File assessorXML) {
        this(null, assessorXML)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject) {
        getParentObject().accessionNumber(
                xnatInterface.queryBase().contentType(ContentType.XML).queryParam('format', 'xml').body(assessorXML.text).
                        post(xnatInterface.formatRestUrl("/projects/${project.id}/subjects/${subject.label}/experiments")).
                        then().assertThat().statusCode(200).and().extract().response().asString().split('/').last()
        )
        final SubjectAssessor createdAssessor = xnatInterface.readExperiment(parentObject.accessionNumber, parentObject.class as Class<Experiment>) as SubjectAssessor
        parentObject.label(createdAssessor.label)
    }

}
