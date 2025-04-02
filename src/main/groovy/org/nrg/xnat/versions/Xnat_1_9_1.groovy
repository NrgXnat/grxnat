package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_8_0

@Follows(Xnat_1_9_0)
class Xnat_1_9_1 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.9.1', '1.9.1.1', '1.9.1.2']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_8_0
    }

}
