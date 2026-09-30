package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_8_0

@Follows(Xnat_1_10_2)
class Xnat_1_10_3 extends XnatVersion {
    @Override
    List<String> getVersionKeys() {
        ['1.10.3']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_8_0
    }
}
