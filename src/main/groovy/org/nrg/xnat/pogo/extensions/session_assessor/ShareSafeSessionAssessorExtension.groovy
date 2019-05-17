package org.nrg.xnat.pogo.extensions.session_assessor

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor

class ShareSafeSessionAssessorExtension extends SessionAssessorQueryPutExtension {

    ShareSafeSessionAssessorExtension(XnatInterface xnatInterface, SessionAssessor sessionAssessor) {
        super(xnatInterface, sessionAssessor)
    }

    @Override
    String url(Project project, Subject subject, ImagingSession session, SessionAssessor sessionAssessor) {
        "/data/projects/${project.id}/subjects/${xnatInterface.getAccessionNumber(subject)}/experiments/${xnatInterface.getAccessionNumber(session)}/assessors/${sessionAssessor.label}"
    }

}
