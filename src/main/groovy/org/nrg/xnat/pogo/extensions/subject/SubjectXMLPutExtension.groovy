package org.nrg.xnat.pogo.extensions.subject

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class SubjectXMLPutExtension extends SubjectExtension {

    private File xmlFile

    SubjectXMLPutExtension(Subject subject, File file) {
        super(subject)
        xmlFile = file
    }

    SubjectXMLPutExtension(File file) {
        this(null, file)
    }

    @Override
    void create(XnatInterface xnatInterface, Project project) {
        final String subjectResponse = xnatInterface.queryBase().queryParam('format', 'xml').contentType(ContentType.XML).
                body(xmlFile.text).post(xnatInterface.projectSubjectsUrl(project)).then().assertThat().statusCode(200).and().extract().response().asString()
        final String subjectId = subjectResponse.split('/').last()
        final Subject createdSubject = xnatInterface.readSubject(subjectId)
        if (parentObject == null) setParentObject(new Subject())
        parentObject.accessionNumber(subjectId).label(createdSubject.label)
    }

}
