package org.nrg.xnat.pogo.extensions.subject_assessor

import com.jayway.restassured.http.ContentType
import org.nrg.testing.CommonUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.util.FileIOUtils

class SubjectAssessorXMLExtension extends SubjectAssessorExtension {

    private File assessorXML
    private XnatInterface xnatInterface

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
                CommonUtils.last(
                        xnatInterface.queryBase().contentType(ContentType.XML).queryParam('format', 'xml').body(FileIOUtils.readFile(assessorXML)).
                                post(xnatInterface.formatRestUrl("/projects/${project.id}/subjects/${subject.label}/experiments")).
                                then().assertThat().statusCode(200).and().extract().response().asString().split('/')
                )
        )
        final SubjectAssessor createdAssessor = xnatInterface.readExperiment(parentObject.accessionNumber, parentObject.getClass()) as SubjectAssessor
        subject.removeExperiment(createdAssessor)
        parentObject.label(createdAssessor.label)
    }

}
