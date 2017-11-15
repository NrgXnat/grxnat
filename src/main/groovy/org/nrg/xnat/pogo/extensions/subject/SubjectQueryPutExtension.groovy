package org.nrg.xnat.pogo.extensions.subject

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.rest.SerializationUtils

class SubjectQueryPutExtension extends SubjectExtension {

    private XnatInterface xnatInterface

    SubjectQueryPutExtension(XnatInterface xnatInterface, Subject subject) {
        super(subject)
        this.xnatInterface = xnatInterface
    }

    @Override
    void create(Project project) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).put(xnatInterface.subjectUrl(project, parentObject)).then().assertThat().statusCode(Matchers.isOneOf(200, 201))
    }

}
