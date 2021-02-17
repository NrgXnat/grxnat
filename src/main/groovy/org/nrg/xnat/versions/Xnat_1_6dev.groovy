package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_6

class Xnat_1_6dev extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.6', '1.6.5', '1.6dev']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_6
    }

}