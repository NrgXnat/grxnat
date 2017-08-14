package org.nrg.xnat.pogo.resources

import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor

class SessionAssessorResource extends Resource {

    SessionAssessorResource(Project project, Subject subject, ImagingSession session, SessionAssessor assessor, String folder) {
        setProject(project)
        setSubject(subject)
        setSubjectAssessor(session)
        if (assessor != null) {
            setSessionAssessor(assessor)
            assessor.addResource(this)
        }
        setFolder(folder)
    }

    SessionAssessorResource(Project project, Subject subject, ImagingSession session, SessionAssessor assessor) {
        this(project, subject, session, assessor, null)
    }

    @Override
    String resourceUrl() {
        "data/projects/${project}/subjects/${subject}/experiments/${subjectAssessor}/assessors/${sessionAssessor}"
    }

}
