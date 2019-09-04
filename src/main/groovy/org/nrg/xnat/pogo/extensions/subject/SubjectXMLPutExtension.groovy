package org.nrg.xnat.pogo.extensions.subject

import com.jayway.restassured.http.ContentType
import org.nrg.testing.CommonStringUtils
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class SubjectXMLPutExtension extends SubjectExtension {

    private File xmlFile
    private XnatInterface xnatInterface

    SubjectXMLPutExtension(XnatInterface xnatInterface, Subject subject, File file) {
        super(subject)
        xmlFile = file
        this.xnatInterface = xnatInterface
    }

    SubjectXMLPutExtension(XnatInterface xnatInterface, File file) {
        this(xnatInterface, null, file)
    }

    @Override
    void create(Project project) {
        final String subjectResponse = xnatInterface.queryBase().queryParam('format', 'xml').contentType(ContentType.XML).
                body(xmlFile.text).post(xnatInterface.projectSubjectsUrl(project)).then().assertThat().statusCode(200).and().extract().response().asString()

        final Subject createdSubject = xnatInterface.readSubject((subjectResponse.split('/').last()))
        if (parentObject == null) setParentObject(new Subject())
        parentObject.accessionNumber(createdSubject.accessionNumber).label(createdSubject.label)
    }

}
