package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan

class CTScan extends Scan {

    CTScan(ImagingSession session, String id) {
        super(session, id)
    }

    CTScan() {
        this(null, null)
    }

}
