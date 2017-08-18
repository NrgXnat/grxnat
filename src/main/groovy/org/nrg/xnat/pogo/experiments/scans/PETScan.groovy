package org.nrg.xnat.pogo.experiments.scans

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.Scan

class PETScan extends Scan {

    PETScan(ImagingSession session, String id) {
        super(session, id)
    }

    PETScan() {
        this(null, null)
    }

}
