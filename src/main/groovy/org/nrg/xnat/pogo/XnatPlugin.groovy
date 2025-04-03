package org.nrg.xnat.pogo

import java.util.function.Function

class XnatPlugin {
    String id
    String name
    String mavenGroupId
    String mavenArtifactId
    String sourceUrl
    String version
    String downloadUrl
    Function<String, String> downloadUrlDerivation

    XnatPlugin(String id, String name, String mavenGroupId, String mavenArtifactId, String sourceUrl) {
        setId(id)
        setName(name)
        setMavenGroupId(mavenGroupId)
        setMavenArtifactId(mavenArtifactId)
        setSourceUrl(sourceUrl)
    }

    XnatPlugin() {}

    XnatPlugin id(String id) {
        setId(id)
        this
    }
    
    XnatPlugin withDownloadUrlDerivation(Function<String, String> downloadUrlDerivation) {
        this.downloadUrlDerivation = downloadUrlDerivation
        this
    }
    
    XnatPlugin ofSpecificVersion(String version) {
        if (downloadUrlDerivation == null) {
            throw new UnsupportedOperationException('Method requires specifying downloadUrlDerivation')
        }
        final XnatPlugin plugin = new XnatPlugin(id, name, mavenGroupId, mavenArtifactId, sourceUrl)
        plugin.setDownloadUrl(downloadUrlDerivation.apply(version))
        plugin.setVersion(version)
        plugin
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        XnatPlugin that = (XnatPlugin) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

}
