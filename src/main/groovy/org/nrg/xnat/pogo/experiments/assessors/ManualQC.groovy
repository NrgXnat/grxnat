package org.nrg.xnat.pogo.experiments.assessors

import org.nrg.xnat.enums.QCStatus
import org.nrg.xnat.jackson.XnatSerialize
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class ManualQC extends SessionAssessor {

    @XnatSerialize QCStatus pass = QCStatus.PASS

    ManualQC(Project project, Subject subject, ImagingSession parentSession, String label) {
        super(project, subject, parentSession, label)
    }

    ManualQC(Project project, Subject subject, ImagingSession parentSession) {
        this(project, subject, parentSession, null)
    }

    ManualQC() {
        this(null, null, null, null)
    }

}
