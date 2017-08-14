package org.nrg.xnat.pogo.extensions

import org.nrg.xnat.pogo.resources.ResourceFile

class SimpleResourceFileExtension extends ResourceFileExtension {

    private File file

    SimpleResourceFileExtension(ResourceFile resourceFile, File file) {
        super(resourceFile)
        this.file = file
    }

    SimpleResourceFileExtension(File file) {
        this(null, file)
    }

    @Override
    File getJavaFile() {
        return file
    }

}
