package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorExtension
import org.nrg.xnat.util.RandomUtils

class SessionAssessor extends Experiment {

    private ImagingSession parentSession

    SessionAssessor(Project project, Subject subject, ImagingSession parentSession, String label) {
        super()
        this.primaryProject = project
        this.subject = subject
        setParentSession(parentSession)
        this.label = label ?: RandomUtils.randomID()
    }

    SessionAssessor() {
        super()
    }

    ImagingSession getParentSession() {
        return parentSession
    }

    void setParentSession(ImagingSession parentSession) {
        this.parentSession = parentSession
        if (parentSession != null) parentSession.addAssessor(this)
    }

    @SuppressWarnings("unchecked")
    <T extends SessionAssessor> T parentSession(ImagingSession parentSession) {
        setParentSession(parentSession)
        return (T)this
    }

    SessionAssessorExtension getExtension() {
        (SessionAssessorExtension) super.extension
    }

    void setExtension(SessionAssessorExtension extension) {
        super.setExtension(extension)
    }

    @SuppressWarnings("unchecked")
    <T extends Experiment> T extension(SessionAssessorExtension extension) {
        super.extension(extension)
    }

}
