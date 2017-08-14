package org.nrg.xnat.interfaces

import org.nrg.xnat.rest.XnatSessionFilter

class XnatInterface_1_6 extends XnatInterface {

    XnatInterface_1_6(XnatSessionFilter sessionFilter) {
        super(sessionFilter)
    }

    @Override
    boolean queryUserAdmin() {
        queryBase().get(formatXnatUrl('/app/template/XDATScreen_admin.vm')).statusCode == 200
    }

    @Override
    String formatXapiUrl(String... components) {
        notSupported()
    }


}
