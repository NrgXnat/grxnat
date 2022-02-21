package org.nrg.xnat.jackson

import org.nrg.xnat.versions.XnatVersion

trait XnatSerializationProvider {

    List<Class<? extends XnatVersion>> getHandledVersions() {
        []
    }

}