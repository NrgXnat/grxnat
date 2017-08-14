package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.DataType

class CTScan extends Scan {

    CTScan(ImagingSession session, String id) {
        super(session, id)
        setXsiType(DataType.CT_SCAN.xsiType)
    }

    CTScan() {
        this(null, null)
    }

}
