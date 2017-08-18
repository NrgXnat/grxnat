package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan

class MRScan extends Scan {

    MRScan(ImagingSession session, String id) {
        super(session, id)
    }

    MRScan() {
        this(null, null)
    }

}
