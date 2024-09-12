package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_8_0

@Follows(Xnat_1_8_10)
class Xnat_1_8_10_1 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.8.10.1']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_8_0
    }

}
