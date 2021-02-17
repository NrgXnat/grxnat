package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface

abstract class XnatVersion {

    abstract List<String> getVersionKeys()

    abstract Class<? extends XnatInterface> getInterfaceClass()

}
