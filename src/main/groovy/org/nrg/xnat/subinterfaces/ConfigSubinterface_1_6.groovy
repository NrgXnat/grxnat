package org.nrg.xnat.subinterfaces

import com.jayway.restassured.http.ContentType
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.SiteConfig
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.Xnat_1_6dev

class ConfigSubinterface_1_6 extends ConfigSubinterface {

    @Override
    List<String> getHandledEndpoints() {
        super.handledEndpoints.findAll { endpoint ->
            !endpoint.startsWith('/xapi')
        } + ['/services/settings']
    }

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        [Xnat_1_6dev]
    }

    @Override
    void postToSiteConfig(Map configSettings) {
        configSettings.each { propertyName, body ->
            queryBase().contentType(ContentType.TEXT).body("${propertyName}=${body}").post(formatRestUrl('/services/settings')).then().assertThat().statusCode(200)
        }
    }

    @Override
    void postToSiteConfig(SiteConfig siteConfig) {
        notSupported() // could be implemented
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
