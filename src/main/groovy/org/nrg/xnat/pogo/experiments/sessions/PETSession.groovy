package org.nrg.xnat.pogo.experiments.sessions

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class PETSession extends ImagingSession {

    String tracer

    PETSession(Project project, Subject subject, String label) {
        super(project, subject, label)
        setDataType(DataType.PET_SESSION)
    }

    PETSession(Project project, Subject subject) {
        this(project, subject, null)
    }

    PETSession(Project project, String label) {
        this(project, null, label)
    }

    PETSession(String label) {
        this(null, null, label)
    }

    PETSession() {
        this(null, (Subject)null)
    }

    PETSession tracer(String tracer) {
        setTracer(tracer)
        return this
    }

}
