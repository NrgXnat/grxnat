package org.nrg.xnat.pogo.experiments.sessions

import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject

class PETMRSession extends ImagingSession {

    String tracer

    PETMRSession(Project project, Subject subject, String label) {
        super(project, subject, label)
    }

    PETMRSession(Project project, Subject subject) {
        this(project, subject, null)
    }

    PETMRSession(Project project, String label) {
        this(project, null, label)
    }

    PETMRSession(String label) {
        this(null, null, label)
    }

    PETMRSession() {
        this(null, (Subject)null)
    }

    PETMRSession tracer(String tracer) {
        setTracer(tracer)
        return this
    }

}
