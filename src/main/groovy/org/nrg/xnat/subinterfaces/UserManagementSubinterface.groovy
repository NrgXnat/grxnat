package org.nrg.xnat.subinterfaces

import io.restassured.response.Response
import org.nrg.xnat.enums.SiteDataRole
import org.nrg.xnat.meta.RequireAdmin
import org.nrg.xnat.pogo.users.User

import static io.restassured.http.ContentType.JSON

class UserManagementSubinterface extends CoreXnatFunctionalitySubinterface {

    private static final String ADMIN_ROLE = 'Administrator'

    @Override
    List<String> getHandledEndpoints() {
        [
                '/user/{USER_ID}/sessions',
                '/users',
                '/xapi/users',
                '/xapi/users/profile/{username}',
                '/xapi/users/{username}',
                '/xapi/users/{username}/enabled/{flag}',
                '/xapi/users/{username}/groups',
                '/xapi/users/{username}/roles',
                '/xapi/users/{username}/verified/{flag}'
        ]
    }

    List<User> readSiteUsers() {
        jsonQueryWithStatusCodeListeners([FORBIDDEN_403])
                .get(formatRestUrl('users'))
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .jsonPath()
                .getObject('ResultSet.Result', User[])
    }

    User readUser(String username) {
        queryBase().get(formatXapiUrl("users/profile/${username}")).then().assertThat().statusCode(200).and().extract().response().as(User)
    }

    @RequireAdmin
    void createUser(User user) {
        queryBase().contentType(JSON).body(user).post(formatXapiUrl('users')).then().assertThat().statusCode(201)
        if (user.isAdmin()) {
            assignUserToRoles(user, ADMIN_ROLE)
        }
        if (user.dataRole != SiteDataRole.NONE) {
            addUserToGroups(user, user.dataRole.name())
        }
    }

    @RequireAdmin
    void updateUser(User user) {
        queryBase().contentType(JSON).body(user).put(formatXapiUrl("users/${user.username}")).then().assertThat().statusCode(200)
    }

    @RequireAdmin
    void verifyUser(User user) {
        queryBase().put(formatXapiUrl("/users/${user.username}/verified/true")).then().assertThat().statusCode(200)
        user.verified(true)
    }

    @RequireAdmin
    void enableUser(User user) {
        queryBase().put(formatXapiUrl("/users/${user.username}/enabled/true")).then().assertThat().statusCode(200)
        user.enabled(true)
    }

    @RequireAdmin
    void assignUserToRoles(User user, String... roles) {
        queryBase().contentType(JSON).body(roles).put(formatXapiUrl("/users/${user.username}/roles")).then().assertThat().statusCode(200)
    }

    @RequireAdmin
    void addUserToGroups(User user, String... groups) {
        queryBase().contentType(JSON).body(groups).put(formatXapiUrl("/users/${user.username}/groups")).then().assertThat().statusCode(200)
    }

    @RequireAdmin
    void removeUserFromGroups(User user, String... groups) {
        queryBase().contentType(JSON).body(groups).delete(formatXapiUrl("/users/${user.username}/groups")).then().assertThat().statusCode(200)
    }

    void makeUserAdmin(User user) {
        assignUserToRoles(user, ADMIN_ROLE)
        addUserToGroups(user, 'ALL_DATA_ADMIN')
        user.admin(true)
    }

    boolean queryUserAdmin() {
        final Response response = queryBase().get(formatXapiUrl("/users/${xnatInterface.authUser.username}/roles"))
        response.statusCode == 200 && ADMIN_ROLE in response.jsonPath().getList('')
    }

    String userSessionsRestUrl(User user) {
        formatRestUrl("user/${user.username}/sessions")
    }

    void expireAllActiveSessions(User targetUser = xnatInterface.authUser) {
        if (xnatInterface.userIsAdmin() || xnatInterface.authUser == targetUser) {
            final int status = queryBase().delete(userSessionsRestUrl(targetUser)).statusCode
            switch (status) {
                case 200:
                    break
                case 403:
                    println 'Attempt to expire active sessions returned a 403. This can occur if the authenticating JSESSION has timed out, so a new one will be generated, and the query repeated.'
                    xnatInterface.regenerateUserSession()
                    queryBase().delete(userSessionsRestUrl(targetUser)).then().assertThat().statusCode(200)
                    break
                default:
                    throw new RuntimeException("Call to expire active sessions returned unexpected status code: ${status}.")
            }
        } else {
            xnatInterface.prohibitNonadmin()
        }
    }

    @SuppressWarnings("GroovyMissingReturnStatement")
    int getNumberActiveSessions(User targetUser = xnatInterface.authUser) {
        if (xnatInterface.userIsAdmin() || xnatInterface.authUser == targetUser) {
            queryBase().get(userSessionsRestUrl(targetUser)).then().assertThat().statusCode(200).assertThat().extract().jsonPath().getInt(targetUser.username)
        } else {
            prohibitNonadmin()
        }
    }

}
