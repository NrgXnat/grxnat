package org.nrg.xnat.pogo.extensions.session_assessor

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.Extension
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.pogo.Subject
import org.nrg.xnat.pogo.experiments.Experiment
import org.nrg.xnat.pogo.experiments.ImagingSession
import org.nrg.xnat.pogo.experiments.SessionAssessor

abstract class SessionAssessorExtension extends Extension<Experiment> {

    SessionAssessorExtension(SessionAssessor sessionAssessor) {
        super(sessionAssessor)
    }

    abstract void create(XnatInterface xnatInterface, Project project, Subject subject, ImagingSession session)

}