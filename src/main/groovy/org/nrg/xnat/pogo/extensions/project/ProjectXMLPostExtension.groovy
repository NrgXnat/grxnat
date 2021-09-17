package org.nrg.xnat.pogo.extensions.project

import io.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project

class ProjectXMLPostExtension extends ProjectXMLPutExtension {

    ProjectXMLPostExtension(Project project, File file) {
        super(project, file)
    }

    ProjectXMLPostExtension(File file) {
        super(file)
    }

    @Override
    void create(XnatInterface xnatInterface) {
        xnatInterface.queryBase().contentType(ContentType.XML).body(xmlFile.text).post(xnatInterface.formatRestUrl('projects')).
                then().assertThat().statusCode(200)
    }

}
