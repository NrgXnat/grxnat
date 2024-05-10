package org.nrg.xnat.subinterfaces

import org.nrg.xnat.versions.plugins.GeneralPluginRequirement

abstract class XnatPluginSubinterface extends XnatFunctionalitySubinterface {

    GeneralPluginRequirement pluginRequirement() {
        new GeneralPluginRequirement()
    }

}
