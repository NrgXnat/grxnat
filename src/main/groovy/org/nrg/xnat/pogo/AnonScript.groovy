package org.nrg.xnat.pogo

import org.apache.commons.lang3.StringUtils
import org.nrg.xnat.enums.DicomEditVersion
import org.nrg.xnat.pogo.extensions.anon.AnonScriptExtension

class AnonScript extends Extensible<AnonScript> {

    private String contents
    DicomEditVersion version
    private static final String NEW_LINE = System.getProperty("line.separator")

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

    /**
     * addStatement
     * Enable scripts to be built from individual statements. Enforce statements to be newLine delimited.
     *
     * @param statement is the statement to concatenate to the script contents
     * @return this AnonScript
     */
    AnonScript addStatement( String statement) {
        if( statement != null) {
            String s = StringUtils.appendIfMissing(statement, NEW_LINE)
            String c = StringUtils.isBlank(contents)? contents : StringUtils.appendIfMissing(contents, NEW_LINE)
            contents = StringUtils.isBlank(c)? s : c.concat(s)
        }
        return this
    }

}
