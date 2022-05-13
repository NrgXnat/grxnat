package org.nrg.xnat.pogo

class XnatPlugin {
    String id
    String name
    String mavenGroupId
    String mavenArtifactId
    String sourceUrl
    String version

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
