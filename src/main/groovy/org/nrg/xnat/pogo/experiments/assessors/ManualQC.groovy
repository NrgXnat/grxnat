package org.nrg.xnat.pogo.experiments.assessors

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class ManualQC extends SessionAssessor {

    ManualQC(Project project, Subject subject, ImagingSession parentSession, String label) {
        super(project, subject, parentSession, label)
        setDataType(DataType.MANUAL_QC)
    }

    ManualQC(Project project, Subject subject, ImagingSession parentSession) {
        this(project, subject, parentSession, null)
    }

    ManualQC() {
        this(null, null, null, null)
    }

}
