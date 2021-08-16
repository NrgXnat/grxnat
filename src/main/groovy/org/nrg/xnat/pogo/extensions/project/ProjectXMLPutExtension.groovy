package org.nrg.xnat.pogo.extensions.project

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project

class ProjectXMLPutExtension extends ProjectExtension {

    protected File xmlFile

    ProjectXMLPutExtension(Project project, File file) {
        super(project)
        xmlFile = file
    }

    ProjectXMLPutExtension(File file) {
        this(null, file)
    }

    @Override
    void create(XnatInterface xnatInterface) {
        xnatInterface.queryBase().queryParam('format', 'xml').contentType(ContentType.XML).body(xmlFile.text).
                put(xnatInterface.projectUrl(parentObject)).then().assertThat().statusCode(200)
    }

}
