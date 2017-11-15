package org.nrg.xnat.interfaces

import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.rest.XnatSessionFilter

class XnatInterface_1_6 extends XnatInterface_1_7_0 {

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

    @Override
    void setSiteAnonScriptStatus(boolean status) {
        prohibitNonadmin()
        queryBase().queryParam('activate', status).put(formatRestUrl('/config/edit/image/dicom/status')).then().assertThat().statusCode(200)
    }

    @Override
    void setSiteAnonScript(AnonScript script) {
        prohibitNonadmin()
        queryBase().body(script.getContents()).put(legacySiteAnonScriptUrl()).then().assertThat().statusCode(200)
    }


}
