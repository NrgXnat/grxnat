package org.nrg.xnat.interfaces

import com.google.common.base.Optional
import org.nrg.xnat.pogo.XnatPlugin

class UnitTestXnatInterface extends XnatInterface {

    UnitTestXnatInterface(String url) {
        super()
        xnatUrl = url
    }

    UnitTestXnatInterface(boolean representsAdminAccess, List<XnatPlugin> representedPlugins) {
        isAdmin = Optional.of(representsAdminAccess) as Optional<Boolean>
        installedPlugins = representedPlugins
    }

}
