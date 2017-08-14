package org.nrg.xnat.pogo.experiments.sessions

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class CTSession extends ImagingSession {

    CTSession(Project project, Subject subject, String label) {
        super(project, subject, label)
        setDataType(DataType.CT_SESSION)
    }

    CTSession(Project project, Subject subject) {
        this(project, subject, null)
    }

    CTSession(Project project, String label) {
        this(project, null, label)
    }

    CTSession(String label) {
        this(null, null, label)
    }

    CTSession() {
        this(null, (Subject)null)
    }
    
}
