package org.nrg.xnat.subinterfaces

import io.restassured.response.Response
import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.pogo.EventTrackingData
import org.nrg.xnat.rest.NotFoundException

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertTrue

class EventTrackingSubinterface extends CoreXnatFunctionalitySubinterface {

    @Override
    List<String> getHandledEndpoints() {
        [
                '/xapi/event_tracking/{key}'
        ]
    }

    EventTrackingData readEventTrackingData(String key) {
        final Response response = queryBase().get(formatXapiUrl('event_tracking', key))
        switch (response.statusCode()) {
            case 200:
                return response.as(EventTrackingData)
            case 404:
                throw new NotFoundException()
            default:
                assertEquals(200, response.statusCode())
        }
    }

    EventTrackingData waitForTrackedEventSuccessful(String key, int maxSeconds = 20 * 60, int pollingMillis = 5000) {
        final StopWatch stopWatch = TimeUtils.launchStopWatch()

        while (true) {
            TimeUtils.checkStopWatch(stopWatch, maxSeconds, 'Tracked event did not complete in time.')

            final EventTrackingData trackingData = readEventTrackingData(key)
            if (trackingData.succeeded != null) {
                assertTrue(trackingData.succeeded)
                return trackingData
            } else {
                TimeUtils.sleep(pollingMillis) // succeeded is null while processing is running, so sleep and keep polling
            }
        }
    }

}
