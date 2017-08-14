package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan
import org.nrg.xnat.pogo.DataType

class PETScan extends Scan {

    PETScan(ImagingSession session, String id) {
        super(session, id)
        setXsiType(DataType.PET_SCAN.xsiType)
    }

    PETScan() {
        this(null, null)
    }

}
