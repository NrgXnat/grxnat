package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_7_4

@Follows(Xnat_1_7_3)
class Xnat_1_7_4 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.7.4']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_7_4
    }

}
