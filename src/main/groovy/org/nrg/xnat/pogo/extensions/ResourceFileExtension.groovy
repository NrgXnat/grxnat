package org.nrg.xnat.pogo.extensions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.rest.SerializationUtils

import java.io.File

abstract class ResourceFileExtension extends Extension<ResourceFile> {

    protected XnatInterface xnatInterface

    ResourceFileExtension(XnatInterface xnatInterface, ResourceFile resourceFile) {
        super(resourceFile)
        this.xnatInterface = xnatInterface
    }

    abstract File getJavaFile()

    void uploadTo(Resource resource) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).multiPart(getJavaFile()).put(xnatInterface.resourceFileUrl(resource, parentObject)).then().assertThat().statusCode(200)
    }

}
