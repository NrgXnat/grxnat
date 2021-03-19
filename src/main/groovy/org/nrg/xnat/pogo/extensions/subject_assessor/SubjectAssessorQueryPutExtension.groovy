package org.nrg.xnat.pogo.extensions.subject_assessor

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.SubjectAssessor
import org.nrg.xnat.rest.SerializationUtils

class SubjectAssessorQueryPutExtension extends SubjectAssessorExtension {

    SubjectAssessorQueryPutExtension(SubjectAssessor subjectAssessor) {
        super(subjectAssessor)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project, Subject subject) {
        if (parentObject.getDataType() == null) {
            throw new UnsupportedOperationException("SubjectAssessor must have xsiType to pass to XNAT for this method")
        }

        parentObject.accessionNumber(
                xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).
                        put(url(xnatInterface, project, subject, parentObject as SubjectAssessor)).
                        then().assertThat().statusCode(Matchers.isOneOf(200, 201)).and().extract().response().asString().trim()
        )
    }

    String url(XnatInterface xnatInterface, Project project, Subject subject, SubjectAssessor subjectAssessor) {
        xnatInterface.subjectAssessorUrl(project, subject, subjectAssessor)
    }

}
