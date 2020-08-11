package org.nrg.xnat.pogo.experiments

import org.nrg.xnat.pogo.HasUri
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.extensions.session_assessor.SessionAssessorExtension
import org.nrg.xnat.util.RandomUtils
import org.nrg.xnat.util.XnatUriUtils

class SessionAssessor extends Experiment implements HasUri {

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

    @Override
    String getUri() {
        final Project preferredProjectObject = {
            if (primaryProject != null) {
                return primaryProject
            } else if (parentSession.primaryProject != null) {
                return parentSession.primaryProject
            } else {
                return subject.primaryProject
            }
        }.call()

        XnatUriUtils.sessionAssessorUri(
                preferredProjectObject.id,
                (subject ?: parentSession.subject).label,
                parentSession.label,
                label,
                true
        )
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
