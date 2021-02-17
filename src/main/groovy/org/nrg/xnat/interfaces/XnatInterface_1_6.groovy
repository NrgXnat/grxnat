package org.nrg.xnat.interfaces

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.AnonScript

class XnatInterface_1_6 extends XnatInterface_1_7_4 {

    @Override
    boolean queryUserAdmin() {
        queryBase().get(formatXnatUrl('/app/template/XDATScreen_admin.vm')).statusCode == 200
    }

    @Override
    String formatXapiUrl(String... components) {
        notSupported()
    }

    @Override
    void postToSiteConfig(Map configSettings) {
        configSettings.each { propertyName, body ->
            queryBase().contentType(ContentType.TEXT).body("${propertyName}=${body}").post(formatRestUrl('/services/settings')).then().assertThat().statusCode(200)
        }
    }

    @Override
    @RequireAdmin
    void setSiteAnonScriptStatus(boolean status) {
        queryBase().queryParam('activate', status).put(formatRestUrl('/config/edit/image/dicom/status')).then().assertThat().statusCode(200)
    }

    @Override
    @RequireAdmin
    void setSiteAnonScript(AnonScript script) {
        queryBase().body(script.getContents()).put(legacySiteAnonScriptUrl()).then().assertThat().statusCode(200)
    }

    @Override
    void setNonadminProjectSetting(boolean allowed) {
        postToSiteConfig(['UI.allow-non-admin-project-creation' : allowed])
    }

}
