package org.nrg.xnat.pogo.extensions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.resources.Resource
import org.nrg.xnat.pogo.resources.ResourceFile
import org.nrg.xnat.rest.SerializationUtils

abstract class ResourceFileExtension extends Extension<ResourceFile> {

    XnatInterface xnatInterface

    ResourceFileExtension(ResourceFile resourceFile) {
        super(resourceFile)
    }

    ResourceFileExtension xnatInterface(XnatInterface xnatInterface) {
        setXnatInterface(xnatInterface)
        this
    }

    abstract File getJavaFile()

    void uploadTo(Resource resource) {
        xnatInterface.queryBase().queryParams(SerializationUtils.serializeToMap(parentObject)).multiPart(getJavaFile()).put(xnatInterface.resourceFileUrl(resource, parentObject)).then().assertThat().statusCode(200)
    }

}
