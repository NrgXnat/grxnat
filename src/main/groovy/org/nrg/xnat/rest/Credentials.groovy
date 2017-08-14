package org.nrg.xnat.rest

import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.AuthenticationSpecification
import com.jayway.restassured.specification.RequestSpecification
import org.nrg.xnat.pogo.users.User

class Credentials {

    static RequestSpecification build(String username, String password, boolean preemptive) {
        if (username == null || password == null) return null

        final AuthenticationSpecification credentials = RestAssured.given().authentication()
        return (preemptive) ? credentials.preemptive().basic(username, password) : credentials.basic(username, password)
    }

    static RequestSpecification build(String username, String password) {
        return build(username, password, true)
    }

    static RequestSpecification build(XnatAliasToken aliasToken) {
        return (aliasToken == null) ? null : build(aliasToken.getAlias(), aliasToken.getSecret())
    }

    static RequestSpecification build(User xnatUser) {
        return build(xnatUser.getUsername(), xnatUser.getPassword())
    }

}
