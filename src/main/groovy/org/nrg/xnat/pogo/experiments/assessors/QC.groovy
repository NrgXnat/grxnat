package org.nrg.xnat.pogo.experiments.assessors

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class QC extends SessionAssessor {

    QC(Project project, Subject subject, ImagingSession parentSession, String label) {
        super(project, subject, parentSession, label)
        setDataType(DataType.QC)
    }

    QC(Project project, Subject subject, ImagingSession parentSession) {
        this(project, subject, parentSession, null)
    }

    QC() {
        this(null, null, null, null)
    }

}
