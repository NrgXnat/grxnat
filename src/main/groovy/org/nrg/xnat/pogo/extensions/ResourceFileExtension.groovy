package org.nrg.xnat.pogo.extensions

import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.resources.ResourceFile

import java.io.File

abstract class ResourceFileExtension extends Extension<ResourceFile> {

    ResourceFileExtension(ResourceFile resourceFile) {
        super(resourceFile)
    }

    abstract File getJavaFile()

}
