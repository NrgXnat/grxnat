package org.nrg.xnat.pogo.experiments.sessions

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class MRSession extends ImagingSession {

    MRSession(Project project, Subject subject, String label) {
        super(project, subject, label)
    }

    MRSession(Project project, Subject subject) {
        this(project, subject, null)
    }

    MRSession(Project project, String label) {
        this(project, null, label)
    }

    MRSession(String label) {
        this(null, null, label)
    }

    MRSession() {
        this(null, (Subject)null)
    }
    
}
