package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class NonimagingAssessor extends SubjectAssessor {

    NonimagingAssessor(Project project, Subject subject, String label) {
        super(project, subject, label)
    }

    NonimagingAssessor() {
        super(null, null, null)
    }

}
