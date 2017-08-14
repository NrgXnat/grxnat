package org.nrg.xnat.pogo.extensions.anon

import com.jayway.restassured.RestAssured
import org.nrg.xnat.pogo.AnonScript

class AnonScriptFromUrlExtension extends AnonScriptExtension {

    AnonScriptFromUrlExtension(AnonScript script, String url) {
        super(script, url)
    }

    AnonScriptFromUrlExtension() {
        super()
    }

    @Override
    String readScript() {
        RestAssured.given().get(locationData).then().assertThat().statusCode(200).and().extract().response().asString()
    }

}

