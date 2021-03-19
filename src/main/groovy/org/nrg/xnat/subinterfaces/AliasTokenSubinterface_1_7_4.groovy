package org.nrg.xnat.subinterfaces

import com.jayway.restassured.response.Response
import org.nrg.xnat.versions.XnatVersion
import org.nrg.xnat.versions.Xnat_1_6dev
import org.nrg.xnat.versions.Xnat_1_7_0
import org.nrg.xnat.versions.Xnat_1_7_1
import org.nrg.xnat.versions.Xnat_1_7_2
import org.nrg.xnat.versions.Xnat_1_7_3
import org.nrg.xnat.versions.Xnat_1_7_4

class AliasTokenSubinterface_1_7_4 extends AliasTokenSubinterface {

    @Override
    List<Class<? extends XnatVersion>> supportedVersions() {
        [Xnat_1_6dev, Xnat_1_7_0, Xnat_1_7_1, Xnat_1_7_2, Xnat_1_7_3, Xnat_1_7_4]
    }

    @Override
    protected boolean validateTokenResponse(Response response, String expectedUsername) {
        response.then().assertThat().statusCode(200)
        if (response.path('valid') == expectedUsername) {
            return true
        } else if (response.asString() == '{}') {
            return false
        } else {
            throw new RuntimeException("Unknown response for alias token validation: ${response.asString()}")
        }
    }

}
