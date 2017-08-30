package org.nrg.xnat.pogo.extensions.anon

import com.jayway.restassured.RestAssured
import org.nrg.xnat.pogo.AnonScript
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.rest.Credentials

class AnonScriptFromUrlExtension extends AnonScriptExtension {

    User authUser

    AnonScriptFromUrlExtension(AnonScript script, String url, User user) {
        super(script, url)
        authUser = user
    }

    AnonScriptFromUrlExtension() {
        super()
    }

    @Override
    String readScript() {
        ((authUser != null) ? Credentials.build(authUser) : RestAssured.given()).get(locationData).then().assertThat().statusCode(200).and().extract().response().asString()
    }

}

