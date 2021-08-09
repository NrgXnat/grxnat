package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_8_0

@Follows(Xnat_1_7_5)
class Xnat_1_7_5_2 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.7.5.2']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_8_0
    }

}
