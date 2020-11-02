package org.nrg.xnat.interfaces

import org.apache.commons.lang3.time.StopWatch
import org.nrg.testing.TimeUtils
import org.nrg.xnat.pogo.experiments.ImagingSession

class XnatInterface_1_7_7 extends XnatInterface_1_8_0 {

    @Override
    String versionIdentifier() {
        '1.7.7'
    }

    @Override
    void waitForAutoRun(ImagingSession session, int maxTimeInSeconds = 60) {
        println("A subsequent operation requires that the AutoRun pipeline complete on this session (${session}) before continuing. If this step appears to be hanging, it likely means that the pipeline engine is not configured correctly on the XNAT server. Waiting for AutoRun completion for up to ${maxTimeInSeconds} seconds...")
        final String accessionNumber = session.accessionNumber ?: getAccessionNumber(session)

        final StopWatch stopWatch = TimeUtils.launchStopWatch()
        while (true) {
            TimeUtils.checkStopWatch(stopWatch, maxTimeInSeconds, "AutoRun did not complete in allotted number of seconds: ${maxTimeInSeconds}")

            final String status = queryBase().queryParam("experiment", accessionNumber).queryParam("format", "json").
                    get(formatRestUrl('services/workflows/AutoRun')).then().extract().jsonPath().getString('items.get(0).data_fields.status')

            if (status == 'Complete') {
                return
            } else if (status == 'Failed') {
                throw new RuntimeException('AutoRun failed.')
            }
            TimeUtils.sleep(1000)
        }
    }

}
