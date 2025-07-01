package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_8_0

@Follows(Xnat_1_9_3)
class Xnat_1_10_0 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.10.0']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_8_0
    }

}
