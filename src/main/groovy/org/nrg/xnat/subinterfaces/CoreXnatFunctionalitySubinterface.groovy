package org.nrg.xnat.subinterfaces

import org.nrg.xnat.versions.XnatVersion

abstract class CoreXnatFunctionalitySubinterface extends XnatFunctionalitySubinterface {

    List<Class<? extends XnatVersion>> supportedVersions() {
        []
    }

}
