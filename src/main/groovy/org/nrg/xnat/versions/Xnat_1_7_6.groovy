package org.nrg.xnat.versions

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.interfaces.XnatInterface_1_7_7

@Follows(Xnat_1_7_5)
class Xnat_1_7_6 extends XnatVersion {

    @Override
    List<String> getVersionKeys() {
        ['1.7.6']
    }

    @Override
    Class<? extends XnatInterface> getInterfaceClass() {
        XnatInterface_1_7_7
    }

}
