package org.nrg.xnat.subinterfaces

import com.jayway.restassured.response.Response
import org.hamcrest.Matchers
import org.nrg.xnat.pogo.users.User
import org.nrg.xnat.rest.PermissionsException
import org.nrg.xnat.rest.XnatAliasToken

import static org.testng.AssertJUnit.assertEquals

class AliasTokenSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/services/tokens/{OPERATION}',
                '/services/tokens/{OPERATION}/user/{USERNAME}',
                '/services/tokens/{OPERATION}/{TOKEN}/{SECRET}'
        ]
    }

    boolean validateAliasToken(XnatAliasToken aliasToken, String expectedUsername) {
        validateTokenResponse(queryBase().get(formatRestUrl('services/tokens/validate', aliasToken.alias, aliasToken.secret)), expectedUsername)
    }

    void invalidateAliasToken(XnatAliasToken aliasToken) {
        queryBase().get(formatRestUrl("/services/tokens/invalidate/${aliasToken.alias}/${aliasToken.secret}")).then().assertThat().statusCode(200)
    }

    String issueAliasTokenUrl(User tokenUser = null) {
        final String proxyTokenSubstring = tokenUser != null ? "/user/${tokenUser.username}" : ''
        formatRestUrl("/services/tokens/issue${proxyTokenSubstring}")
    }

    XnatAliasToken generateAliasToken(User tokenUser = null) throws PermissionsException {
        final Response response = queryBase().get(issueAliasTokenUrl(tokenUser))
        switch (response.statusCode) {
            case 200:
                return response.then().extract().jsonPath().getObject('', XnatAliasToken) // see: XNAT-5498
            case 403:
                response.then().body(Matchers.containsString('admin'))
                throw new PermissionsException()
            default:
                throw new RuntimeException("Unknown response code for alias token creation: ${response.statusCode}")

        }
    }

    protected boolean validateTokenResponse(Response response, String expectedUsername) {
        switch (response.statusCode) {
            case 404:
                return false
            case 200:
                assertEquals(expectedUsername, response.then().extract().jsonPath().getString('valid'))
                return true
            default:
                throw new RuntimeException("Unknown response code for alias token validation: ${response.statusCode}")
        }
    }

}
