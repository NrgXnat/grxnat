package org.nrg.xnat.pogo.extensions.subject

import org.hamcrest.Matchers
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.rest.SerializationUtils

class SubjectQueryPutExtension extends SubjectExtension {

    SubjectQueryPutExtension(Subject subject) {
        super(subject)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project) {
        parentObject.accessionNumber(
                xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).
                        put(xnatInterface.subjectUrl(project, parentObject as Subject)).
                        then().assertThat().statusCode(Matchers.isOneOf(200, 201)).
                        and().extract().response().asString().trim()
        )
    }

}
