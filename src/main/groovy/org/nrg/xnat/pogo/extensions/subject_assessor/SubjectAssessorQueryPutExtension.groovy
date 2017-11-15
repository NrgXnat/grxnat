package org.nrg.xnat.pogo.extensions.subject_assessor

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.rest.SerializationUtils

class SubjectAssessorQueryPutExtension extends SubjectAssessorExtension {

    private XnatInterface xnatInterface

    SubjectAssessorQueryPutExtension(XnatInterface xnatInterface, SubjectAssessor subjectAssessor) {
        super(subjectAssessor)
        this.xnatInterface = xnatInterface
    }

    @Override
    void create(Project project, Subject subject) {
        if (parentObject.getDataType() == null) {
            throw new UnsupportedOperationException("SubjectAssessor must have xsiType to pass to XNAT for this method")
        }

        parentObject.accessionNumber(
                xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).
                        put(xnatInterface.formatRestUrl("/projects/${project.id}/subjects/${subject.label}/experiments/${parentObject.label}")).
                        then().assertThat().statusCode(Matchers.isOneOf(200, 201)).and().extract().response().asString().trim()
        )
    }

}
