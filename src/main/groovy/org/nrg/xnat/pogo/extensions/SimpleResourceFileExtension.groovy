package org.nrg.xnat.pogo.extensions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.resources.ResourceFile

class SimpleResourceFileExtension extends ResourceFileExtension {

    private File file

    SimpleResourceFileExtension(XnatInterface xnatInterface, ResourceFile resourceFile, File file) {
        super(xnatInterface, resourceFile)
        this.file = file
    }

    SimpleResourceFileExtension(XnatInterface xnatInterface, File file) {
        this(xnatInterface, null, file)
    }

    @Override
    File getJavaFile() {
        file
    }

}
