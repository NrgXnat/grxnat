package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.DataType

class MRScan extends Scan {

    MRScan(ImagingSession session, String id) {
        super(session, id)
        setXsiType(DataType.MR_SCAN.xsiType)
    }

    MRScan() {
        this(null, null)
    }

}
