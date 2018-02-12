package org.nrg.xnat.pogo.resources

import com.fasterxml.jackson.annotation.JsonProperty
import org.nrg.xnat.pogo.Extensible
import org.nrg.xnat.pogo.extensions.ResourceFileExtension

class ResourceFile extends Extensible<ResourceFile> {

    String name
    Resource resourceFolder
    boolean unzip = false
    String content
    String format
    String uri
    @JsonProperty('digest') String md5

    ResourceFile(Resource resourceFolder, String name) {
        this.resourceFolder = resourceFolder
        resourceFolder.addResourceFile(this)
        this.name = name
    }

    ResourceFile() {}

    ResourceFileExtension getExtension() {
        super.getExtension() as ResourceFileExtension
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

    String fullPath() {
        (uri == null) ? name : uri.drop(uri.indexOf('/files/') + 7) // set path equal to what's after /files/ in the URI. TODO: make this not break in pathological case if a component in the URL (such as project) is equal to 'files' (I don't think this is really possible to do)
    }

    ResourceFile name(String name) {
        setName(name)
        this
    }

    ResourceFile resourceFolder(Resource resourceFolder) {
        setResourceFolder(resourceFolder)
        this
    }

    ResourceFile unzip(boolean unzip) {
        setUnzip(unzip)
        this
    }

    ResourceFile content(String content) {
        setContent(content)
        this
    }

    ResourceFile fileFormat(String format) {
        setFormat(format)
        this
    }

    ResourceFile uri(String uri) {
        setUri(uri)
        this
    }

    ResourceFile md5(String md5) {
        setMd5(md5)
        this
    }

    ResourceFile extension(ResourceFileExtension extension) {
        setExtension(extension)
        this
    }

}
