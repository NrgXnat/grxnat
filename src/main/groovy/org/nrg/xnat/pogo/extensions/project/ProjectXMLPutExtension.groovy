package org.nrg.xnat.pogo.extensions.project

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.util.FileIOUtils

class ProjectXMLPutExtension extends ProjectExtension {

    private File xmlFile
    private XnatInterface xnatInterface

    ProjectXMLPutExtension(XnatInterface xnatInterface, Project project, File file) {
        super(project)
        xmlFile = file
        this.xnatInterface = xnatInterface
    }

    ProjectXMLPutExtension(XnatInterface xnatInterface, File file) {
        this(xnatInterface, null, file)
    }

    @Override
    void create() {
        xnatInterface.queryBase().queryParam('format', 'xml').contentType(ContentType.XML).body(FileIOUtils.readFile(xmlFile)).
                put(xnatInterface.projectUrl(parentObject)).then().assertThat().statusCode(200)
    }

}
