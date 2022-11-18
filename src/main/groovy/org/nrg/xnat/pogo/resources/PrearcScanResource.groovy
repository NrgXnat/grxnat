package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.prearchive.SessionData

class PrearcScanResource extends Resource {

    PrearcScanResource(SessionData sessionData, Scan scan) {
        setSessionData(sessionData)
        setScan(scan)
    }

    PrearcScanResource() {}

    @Override
    String resourceUrl() {
        "data${sessionData.url}/scans/${scan.id}"
    }

}
