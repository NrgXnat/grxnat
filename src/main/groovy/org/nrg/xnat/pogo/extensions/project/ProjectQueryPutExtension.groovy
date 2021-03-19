package org.nrg.xnat.pogo.extensions.project

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.rest.SerializationUtils

class ProjectQueryPutExtension extends ProjectExtension {

    ProjectQueryPutExtension(Project project) {
        super(project)
    }

    @Override
    void create(XnatInterface xnatInterface) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).put(xnatInterface.projectUrl(parentObject as Project)).then().assertThat().statusCode(200)
    }

}
