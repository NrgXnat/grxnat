package org.nrg.xnat.pogo.extensions.anon

import org.nrg.xnat.pogo.AnonScript

class AnonScriptFileExtension extends AnonScriptExtension {

    private File file

    AnonScriptFileExtension(AnonScript script, File file) {
        super(script, null)
        this.file = file
    }

    AnonScriptFileExtension(AnonScript script, String file) {
        this(script, new File(file))
    }

    AnonScriptFileExtension() {
        super()
    }

    @Override
    String readScript() {
        file.text
    }

}
