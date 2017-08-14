package org.nrg.xnat.pogo

import org.nrg.xnat.enums.DicomEditVersion
import org.nrg.xnat.pogo.extensions.anon.AnonScriptExtension

class AnonScript extends Extensible<AnonScript> {

    private String contents
    DicomEditVersion version

    AnonScript(String contents, DicomEditVersion version) {
        setContents(contents)
        setVersion(version)
    }

    AnonScript() {}

    String getContents() {
        if (contents != null) return contents // cache contents

        if (extension == null) {
            throw new RuntimeException("You must either provide contents for an anon script, or extend AnonScriptExtension.class and provide a way to read a script in.")
        }
        contents = extension.readScript()
        return contents
    }

    void setContents(String contents) {
        this.contents = contents
    }

    AnonScriptExtension getExtension() {
        return (AnonScriptExtension) super.getExtension()
    }

    void setExtension(AnonScriptExtension extension) {
        super.setExtension(extension)
    }

    AnonScript contents(String contents) {
        setContents(contents)
        return this
    }

    AnonScript version(DicomEditVersion version) {
        setVersion(version)
        return this
    }

    AnonScript extension(AnonScriptExtension extension) {
        setExtension(extension)
        return this
    }

}
