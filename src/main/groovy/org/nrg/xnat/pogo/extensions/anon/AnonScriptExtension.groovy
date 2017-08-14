package org.nrg.xnat.pogo.extensions.anon

import org.nrg.xnat.pogo.Extension

import org.nrg.xnat.pogo.AnonScript

abstract class AnonScriptExtension extends Extension<AnonScript> {

    String locationData
    AnonScript script

    abstract String readScript()

    AnonScriptExtension(AnonScript script, String locationData) {
        setScript(script)
        if (script != null) script.extension(this)
        setLocationData(locationData)
    }

    AnonScriptExtension() {}

}
