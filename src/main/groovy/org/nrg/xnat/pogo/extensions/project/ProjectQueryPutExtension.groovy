package org.nrg.xnat.pogo.extensions.project

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.rest.SerializationUtils

class ProjectQueryPutExtension extends ProjectExtension {

    private XnatInterface xnatInterface

    ProjectQueryPutExtension(XnatInterface xnatInterface, Project project) {
        super(project)
        this.xnatInterface = xnatInterface
    }

    @Override
    void create() {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).put(xnatInterface.projectUrl(parentObject)).then().assertThat().statusCode(200)
    }

}
