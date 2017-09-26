package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.extensions.ResourceFileExtension

class ResourceFile extends Extensible<ResourceFile> {

    String name
    Resource resourceFolder
    boolean unzip = false
    String content

    ResourceFile(Resource resourceFolder, String name) {
        this.resourceFolder = resourceFolder
        this.name = name
    }

    ResourceFile() {}

    ResourceFileExtension getExtension() {
        return (ResourceFileExtension) super.getExtension()
    }

    void setExtension(ResourceFileExtension extension) {
        super.setExtension(extension)
    }

    String getName() {
        if (name != null) {
            return name
        } else if (extension != null && extension.javaFile != null) {
            return extension.javaFile.name
        }
        null
    }

    ResourceFile name(String name) {
        setName(name)
        return this
    }

    ResourceFile resourceFolder(Resource resourceFolder) {
        setResourceFolder(resourceFolder)
        return this
    }

    ResourceFile unzip(boolean unzip) {
        setUnzip(unzip)
        return this
    }

    ResourceFile content(String content) {
        setContent(content)
        return this
    }

    ResourceFile extension(ResourceFileExtension extension) {
        setExtension(extension)
        return this
    }

}
