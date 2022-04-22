package org.nrg.xnat.subinterfaces

import io.restassured.http.ContentType
import org.nrg.xnat.enums.ShareMethod
import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.meta.RequirePlugin
import org.nrg.xnat.pogo.EventTrackingData
import org.nrg.xnat.pogo.PluginRegistry
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.sharing.BatchSharePayload
import org.nrg.xnat.pogo.sharing.ShareRequest

class EventTrackingSubinterface extends XnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/event_tracking/{key}'
        ]
    }

    EventTrackingData readEventTrackingData(String key) {
        queryBase().get(formatXapiUrl('event_tracking', key)).then().assertThat().statusCode(200).
            and().extract().as(EventTrackingData)
    }

}
