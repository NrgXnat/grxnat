package org.nrg.xnat.subinterfaces

import org.nrg.xnat.meta.RequireAdmin

import static io.restassured.http.ContentType.JSON

class NotificationsSubinterface extends CoreXnatFunctionalitySubinterface {

    public static final SMTP_ENABLED = 'smtpEnabled'

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/notifications'
        ]
    }

    @RequireAdmin
    Map<String, Object> readNotificationsConfigurationAsMap() {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403]).contentType(JSON).get(formatXapiUrl('notifications'))
                .then().assertThat().statusCode(200).and().extract().as(Map)
    }

    @RequireAdmin
    void postToNotificationsConfiguration(Map<String, Object> settings) {
        queryBaseWithStatusCodeListeners([FORBIDDEN_403]).contentType(JSON).body(settings).post(formatXapiUrl('notifications'))
                .then().assertThat().statusCode(200)
    }

    @RequireAdmin
    void disableSmtp() {
        postToNotificationsConfiguration([(SMTP_ENABLED): false])
    }

    @RequireAdmin
    void enableSmtp() {
        postToNotificationsConfiguration([(SMTP_ENABLED): true])
    }

}
