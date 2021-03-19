package org.nrg.xnat.subinterfaces

import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.Xnat_1_6dev

class UserManagementSubinterface_1_6 extends UserManagementSubinterface {

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        [Xnat_1_6dev]
    }

    @Override
    boolean queryUserAdmin() {
        queryBase().get(formatXnatUrl('/app/template/XDATScreen_admin.vm')).statusCode == 200
    }

}
