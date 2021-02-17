package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_7_4

@Follows(Xnat_1_6dev)
class Xnat_1_7_0 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.7.0']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_7_4
    }

}
