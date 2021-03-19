package org.nrg.xnat.interfaces

import com.google.common.base.Optional
import org.nrg.xnat.pogo.XnatPlugin
import org.nrg.xnat.versions.XnatVersion

class UnitTestXnatInterface extends XnatInterface {

    UnitTestXnatInterface(String url, Class<? extends XnatVersion> versionClass = null) {
        super()
        xnatUrl = url
        if (versionClass != null) {
            fromVersion(versionClass)
        }
    }

    UnitTestXnatInterface(boolean representsAdminAccess, List<XnatPlugin> representedPlugins, Class<? extends XnatVersion> versionClass = null) {
        isAdmin = Optional.of(representsAdminAccess) as Optional<Boolean>
        installedPlugins = representedPlugins
        if (versionClass != null) {
            fromVersion(versionClass)
        }
    }

}
